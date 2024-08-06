import ujson.Value

import upickle.core.LinkedHashMap

package object enrichment {

  implicit class ExtSeq[A](ls: Seq[A]) {
    def deleteFirstMatch(value: A): Seq[A] = {
      val index = ls.indexOf(value)  //index is -1 if there is no match
      if (index < 0) {
        ls
      } else if (index == 0) {
        ls.tail
      } else {
        // splitAt keeps the matching element in the second group
        val (a: Seq[A], b: Seq[A]) = ls.splitAt(index)
        a ++ b.tail
      }
    }
  }

  implicit class ExtUPickleHashMap(ls: LinkedHashMap[String, Value]) {
    def addPairs(elems: (String, Value)*): LinkedHashMap[String, Value] = {
      ls.addAll(elems)
    }
  }
}
