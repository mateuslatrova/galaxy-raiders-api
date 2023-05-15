@file:Suppress("UNUSED_PARAMETER") // <- REMOVE
package galaxyraiders.core.physics

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
    var dx = Math.abs(this.x-p.x)
    var dy = Math.abs(this.y-p.y)
    return Vector2D(dx, dy)
  }

  fun impactDirection(p: Point2D): Vector2D {
    return this.contactDirection(p).normal
  }

  fun contactVector(p: Point2D): Vector2D {
    return (p - this).toVector()
  }

  fun contactDirection(p: Point2D): Vector2D {
    var vector = this.contactVector(p)
    var direction = vector.unit
    return direction
  }

  fun distance(p: Point2D): Double {
    var horizontalDistance = this.x - p.x
    var verticalDistance = this.y - p.y,2
    var squaredDistance = pow(horizontalDistance,2) + pow(verticalDistance,2)
    return sqrt(squaredDistance)
  }
}
