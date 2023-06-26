package galaxyraiders.core.game

import galaxyraiders.core.physics.Point2D
import galaxyraiders.core.physics.Vector2D
import kotlin.math.pow

class Explosion : SpaceObject {
    var is_triggered: Boolean = false
    var score: Double = 0.
    var asteroid: Asteroid 
    var missile: Missile

    constructor(
        asteroid: Asteroid,
        missile: Missile
    ) : super("Explosion", '*', asteroid.center, Vector2D(0.,0.), asteroid.radius, 0.) {
        this.asteroid = asteroid
        this.missile = missile
        this.score = this.calculateScore()
    }

    fun calculateScore(): Double {
        return this.mass / this.radius.pow(2)
    }

    override fun toString(): String {
        return "${this.type} at ${this.center}, triggered: $is_triggered"
    }

    fun trigger() {
        is_triggered = true
    }

    fun untrigger() {
        is_triggered = false
    }
}
