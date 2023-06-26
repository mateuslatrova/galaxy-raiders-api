package galaxyraiders.core.game

import galaxyraiders.Config
import galaxyraiders.ports.RandomGenerator
import galaxyraiders.ports.ui.Controller
import galaxyraiders.ports.ui.Controller.PlayerCommand
import galaxyraiders.ports.ui.Visualizer
import java.io.File
import java.time.LocalTime
import kotlin.system.measureTimeMillis
import org.json.JSONArray

const val MILLISECONDS_PER_SECOND: Int = 1000

object GameEngineConfig {
  private val config = Config(prefix = "GR__CORE__GAME__GAME_ENGINE__")

  val frameRate = config.get<Int>("FRAME_RATE")
  val spaceFieldWidth = config.get<Int>("SPACEFIELD_WIDTH")
  val spaceFieldHeight = config.get<Int>("SPACEFIELD_HEIGHT")
  val asteroidProbability = config.get<Double>("ASTEROID_PROBABILITY")
  val coefficientRestitution = config.get<Double>("COEFFICIENT_RESTITUTION")

  val msPerFrame: Int = MILLISECONDS_PER_SECOND / this.frameRate
}

@Suppress("TooManyFunctions")
class GameEngine(
    val generator: RandomGenerator,
    val controller: Controller,
    val visualizer: Visualizer,
) {
  val field =
      SpaceField(
          width = GameEngineConfig.spaceFieldWidth,
          height = GameEngineConfig.spaceFieldHeight,
          generator = generator
      )

  var playing = true

  val gameStartTime = LocalTime.now()

  var scoreboard = this.readScoreboard()

  fun execute() {
    while (true) {
      val duration = measureTimeMillis { this.tick() }

      Thread.sleep(maxOf(0, GameEngineConfig.msPerFrame - duration))
    }
  }

  fun execute(maxIterations: Int) {
    repeat(maxIterations) { this.tick() }
  }

  fun tick() {
    this.processPlayerInput()
    this.updateSpaceObjects()
    this.updateScoreboard()
    this.updateLeaderboard()
    this.renderSpaceField()
  }

  fun processPlayerInput() {
    this.controller.nextPlayerCommand()?.also {
      when (it) {
        PlayerCommand.MOVE_SHIP_UP -> this.field.ship.boostUp()
        PlayerCommand.MOVE_SHIP_DOWN -> this.field.ship.boostDown()
        PlayerCommand.MOVE_SHIP_LEFT -> this.field.ship.boostLeft()
        PlayerCommand.MOVE_SHIP_RIGHT -> this.field.ship.boostRight()
        PlayerCommand.LAUNCH_MISSILE -> this.field.generateMissile()
        PlayerCommand.PAUSE_GAME -> this.playing = !this.playing
      }
    }
  }

  fun updateSpaceObjects() {
    if (!this.playing) return
    this.handleCollisions()
    this.moveSpaceObjects()
    this.handleExplosions()
    this.trimSpaceObjects()
    this.generateAsteroids()
  }

  fun handleExplosions() {
    this.field.disableAlreadyTriggeredExplosions()
    this.field.triggerNewExplosions()
  }

  fun handleCollisions() {
    this.field.spaceObjects.forEachPair { (first, second) ->
      if (first.impacts(second)) {
        first.collideWith(second, GameEngineConfig.coefficientRestitution)
      }
    }
  }

  fun moveSpaceObjects() {
    this.field.moveShip()
    this.field.moveAsteroids()
    this.field.moveMissiles()
  }

  fun trimSpaceObjects() {
    this.field.trimAsteroids()
    this.field.trimMissiles()
    this.field.trimExplosions()
  }

  fun generateAsteroids() {
    val probability = generator.generateProbability()

    if (probability <= GameEngineConfig.asteroidProbability) {
      this.field.generateAsteroid()
    }
  }

  fun renderSpaceField() {
    this.visualizer.renderSpaceField(this.field)
  }

  fun updateScoreboard() {
    val scoreboardList = this.readBoardJsonFile("core/score/Scoreboard.json")
    val currentResult = this.getResultForScoreboard()
    if (this.currentResultWasAlreadyRegistered(scoreboardList)) {
      scoreboardList.removeAt(scoreboardList.size - 1)
    }
    scoreboardList += currentResult
    this.writeBoardListToJsonFile(scoreboardList, "core/score/Scoreboard.json")
  }

  fun readBoardJsonFile(filepath: String): MutableList<Map<String, Any>> {
    val jsonFile = File(filepath)
    if (jsonFile.length() == 0L) {
      return mutableListOf<Map<String, Any>>()
    } else {
      val jsonString = jsonFile.readText()
      val jsonArray = JSONArray(jsonString)
      val boardList: MutableList<Map<String, Any>> = this.convertJSONArrayToListOfDicts(jsonArray)
      return boardList
    }
  }

  fun convertJSONArrayToListOfDicts(jsonArray: JSONArray): MutableList<Map<String, Any>> {
    val list: MutableList<Map<String, Any>> = mutableListOf()
    for (i in 0 until jsonArray.length()) {
      val jsonObject = jsonArray.getJSONObject(i)
      val keys = jsonObject.keys()
      val dict: MutableMap<String, Any> = mutableMapOf()
      while (keys.hasNext()) {
        val key = keys.next() as String
        val value = jsonObject.get(key)
        dict[key] = value
      }
      list.add(dict)
    }
    return list
  }

  fun getResultForScoreboard(): MutableMap<String, Any> {
    val resultDict: MutableMap<String, Any> = mutableMapOf()
    resultDict["gameStartTime"] = this.gameStartTime
    resultDict["finalScore"] = this.getCurrentScore()
    resultDict["asteroidsHit"] = this.getNumberOfHitAsteroids()
    return resultDict
  }

  fun getCurrentScore(): Double {
    var currentScore = 0.0
    this.field.explosions.forEach { currentScore += it.score }
    return currentScore
  }

  fun getNumberOfHitAsteroids(): Int {
    return this.field.explosions.size
  }

  fun currentResultWasAlreadyRegistered(scoreboardList: MutableList<Map<String, Any>>): Boolean {
    val lastResult = scoreboardList.last()
    if (lastResult["gameStartTime"] == this.gameStartTime) return true else return false
  }

  fun writeBoardListToJsonFile(boardList: MutableList<Map<String, Any>>, filepath: String) {
    val jsonFile = File(filepath)
    jsonFile.writeText(JSONArray(boardList).toString())
  }

  fun updateLeaderboard() {
    val scoreboardList = this.readBoardJsonFile("core/score/Scoreboard.json")
    scoreboardList = mutableListOf(scoreboardList.sortedByDescending { it["finalScore"] })
    val leaderboardList = scoreboardList.take(3)
    this.writeBoardListToJsonFile(leaderboardList, "core/score/Leaderboard.json")
  }
}

fun <T> List<T>.forEachPair(action: (Pair<T, T>) -> Unit) {
  for (i in 0 until this.size) {
    for (j in i + 1 until this.size) {
      action(Pair(this[i], this[j]))
    }
  }
}
