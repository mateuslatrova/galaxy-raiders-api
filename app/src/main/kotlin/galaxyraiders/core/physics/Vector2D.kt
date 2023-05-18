@file:Suppress("UNUSED_PARAMETER") // <- REMOVE

package galaxyraiders.core.physics

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlin.math.asin
import kotlin.math.pow
import kotlin.math.sqrt

const val RADIANT_TO_DEGREE_CONVERSION_FACTOR = 180 / Math.PI

@JsonIgnoreProperties("unit", "normal", "degree", "magnitude")
data class Vector2D(val dx: Double, val dy: Double) {
  override fun toString(): String {
    return "Vector2D(dx=$dx, dy=$dy)"
  }

  val magnitude: Double
    get() = sqrt(dx.pow(2) + dy.pow(2))

  val radiant: Double
    get() {
      // First quadrant
      if (dx > 0 && dy > 0) return asin(dy / magnitude)
      // Second quadrant
      else if (dx < 0 && dy > 0) return Math.PI - asin(dy / magnitude)
      // Third quadrant
      else if (dx < 0 && dy < 0) return -Math.PI + asin(-dy / magnitude)
      // Fourth quadrant
      else return -asin(-dy / magnitude)
    }

  val degree: Double
    get() = RADIANT_TO_DEGREE_CONVERSION_FACTOR * radiant

  val unit: Vector2D
    get() = this.div(magnitude)

  val normal: Vector2D
    get() = Vector2D(this.unit.dy, -this.unit.dx)

  operator fun times(scalar: Double): Vector2D {
    var multipliedDx = dx * scalar
    var multipliedDy = dy * scalar
    return Vector2D(multipliedDx, multipliedDy)
  }

  operator fun div(scalar: Double): Vector2D {
    var dividedDx = dx / scalar
    var dividedDy = dy / scalar
    return Vector2D(dividedDx, dividedDy)
  }

  operator fun times(v: Vector2D): Double {
    var dotProduct = (dx * v.dx) + (dy * v.dy)
    return dotProduct
  }

  operator fun plus(v: Vector2D): Vector2D {
    var sumOfDxs = dx + v.dx
    var sumOfDys = dy + v.dy
    return Vector2D(sumOfDxs, sumOfDys)
  }

  operator fun plus(p: Point2D): Point2D {
    var sumOfXs = dx + p.x
    var sumOfYs = dy + p.y
    return Point2D(sumOfXs, sumOfYs)
  }

  operator fun unaryMinus(): Vector2D {
    var invertedDx = -dx
    var invertedDy = -dy
    return Vector2D(invertedDx, invertedDy)
  }

  operator fun minus(v: Vector2D): Vector2D {
    var subtractionOfDxs = dx - v.dx
    var subtractionOfDys = dy - v.dy
    return Vector2D(subtractionOfDxs, subtractionOfDys)
  }

  fun scalarProject(target: Vector2D): Double {
    return this.times(target) / target.magnitude
  }

  fun vectorProject(target: Vector2D): Vector2D {
    return this.scalarProject(target) * target.unit
  }
}

operator fun Double.times(v: Vector2D): Vector2D {
  var multipliedX = v.dx * this
  var multipliedY = v.dy * this
  return Vector2D(multipliedX, multipliedY)
}
