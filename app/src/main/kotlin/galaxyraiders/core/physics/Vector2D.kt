@file:Suppress("UNUSED_PARAMETER") // <- REMOVE
package galaxyraiders.core.physics

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties("unit", "normal", "degree", "magnitude")
data class Vector2D(val dx: Double, val dy: Double) {
  override fun toString(): String {
    return "Vector2D(dx=$dx, dy=$dy)"
  }

  val magnitude: Double
    get() = sqrt(pow(dx, 2) + pow(dy, 2))

  val radiant: Double
    get() = {
      // First quadrant
      if (dx > 0 && dy > 0)
        asin(y/magnitude)
      // Second quadrant
      else if (dx < 0 && dy > 0)
        Math.PI - asin(y/magnitude)
      // Third quadrant
      else if (dx < 0 && dy < 0)
        -Math.PI + asin(-y/magnitude)
      // Fourth quadrant
      else if (dx > 0 && dy < 0)
        -asin(-y/magnitude)
    }

  val degree: Double
    get() = 180 * radiant / Math.PI

  val unit: Vector2D
    get() = this.div(magnitude)

  val normal: Vector2D
    get() = Vector2D(unit.dy, unit.dx)

  operator fun times(scalar: Double): Vector2D {
    var multipliedX = this.x * scalar
    var multipliedY = this.y * scalar
    return Vector2D(multipliedX, multipliedY)
  }

  operator fun div(scalar: Double): Vector2D {
    var dividedX = this.x / scalar
    var dividedY = this.y / scalar
    return Vector2D(dividedX, dividedY)
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
    return (this * target.unit) * target
  }
}

operator fun Double.times(v: Vector2D): Vector2D {
  var multipliedX = v.dx * this
  var multipliedY = v.dy * this
  return Vector2D(multipliedX, multipliedY)
}
