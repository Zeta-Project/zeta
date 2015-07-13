class window.Bounds

  @TOO_LOW: -1
  @IN_BOUNDS: 0
  @TOO_HIGH: 1


  constructor: (@lowerBound, @upperBound) ->


  getLowerBound: () ->
    @lowerBound


  getUpperBound: () ->
    @upperBound


  compareTo: (count) ->
    check = window.Bounds.IN_BOUNDS

    if count < @lowerBound
      check = window.Bounds.TOO_LOW
    else if @upperBound != -1 && count > @upperBound
      check = window.Bounds.TOO_HIGH

    check