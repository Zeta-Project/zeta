package generator.model.style

/**
 * Created by julian on 29.10.15.
 */
abstract class GradientRef(val name: String,
                           val description: String,
                           val area: List[GradientColorArea]) extends ColorOrGradient
