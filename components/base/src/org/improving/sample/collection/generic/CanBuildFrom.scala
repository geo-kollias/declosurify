/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2003-2011, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package org.improving.sample.collection
package generic

import mutable.Builder

trait CanBuildFrom[-From, -Elem, +To] {
  def apply(from: From): Builder[Elem, To]
  def apply(): Builder[Elem, To]
}
