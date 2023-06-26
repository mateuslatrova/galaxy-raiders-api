@file:Suppress("UNUSED_PARAMETER") // <- REMOVE

package galaxyraiders.core.physics

import kotlin.math.pow
import kotlin.math.sqrt

data class Point2D(val x: Double, val y: Double) {
  operator fun plus(p: Point2D): Point2D {
    var sumOfXs = x + p.x
    var sumOfYs = y + p.y
    return Point2D(sumOfXs, sumOfYs)
  }

  operator fun plus(v: Vector2D): Point2D {
    var sumOfXs = x + v.dx
    var sumOfYs = y + v.dy
    return Point2D(sumOfXs, sumOfYs)
  }

  override fun toString(): String {
    return "Point2D(x=$x, y=$y)"
  }

  fun toVector(): Vector2D {
    return Vector2D(this.x, this.y)
  }

  fun impactVector(p: Point2D): Vector2D {
    var impactPoint = Point2D(p.x - x, p.y - y)
    return impactPoint.toVector()
  }

  fun impactDirection(p: Point2D): Vector2D {
    var vector = this.impactVector(p)
    var direction = vector.unit
    return direction
  }

  fun contactVector(p: Point2D): Vector2D {
    return this.impactVector(p).normal
  }

  fun contactDirection(p: Point2D): Vector2D {
    return this.impactDirection(p).normal
  }

  fun distance(p: Point2D): Double {
    var horizontalDistance: Double = this.x - p.x
    var verticalDistance: Double = this.y - p.y
    var squaredDistance = horizontalDistance.pow(2) + verticalDistance.pow(2)
    return sqrt(squaredDistance)
  }
}
