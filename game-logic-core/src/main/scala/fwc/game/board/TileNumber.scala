package fwc.game.board

type TileNumber = Int

extension (t: TileNumber)
  def isValid: Boolean = t >= 0 && t < 58