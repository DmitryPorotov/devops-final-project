package fwc.gameLoading

import fwc.game.houses.HouseType

case class BoardStart(house: HouseType,
                      map: Seq[BoardStartTile],
                      tracks: BoardStartTracks)

case class BoardStartTracks(throne: Int,
                            fiefdoms: Int,
                            court: Int,
                            supply: Int,
//                            victory: Int
                           )

case class BoardStartTile(tileNumber: Int,
                          ships: Int = 0,
                          knights: Int = 0,
                          footmen: Int = 0,
                          garrison: Int = 0)
