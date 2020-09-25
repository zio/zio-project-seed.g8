package $name;format="space,package,lower"$

import zio.App
import zio.console._

object HelloWorld extends App {

  def run(args: List[String]) =
    myAppLogic.exitCode

  val myAppLogic =
    for {
      _    <- putStrLn("Hello! What is your name?")
      name <- getStrLn
      _    <- putStrLn(s"Hello, \$name, welcome to ZIO!")
    } yield ()
}
