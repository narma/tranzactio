package samples

import zio.test.{Spec, _}
import zio.{Chunk, Scope, ULayer, ZIOAppArgs, ZIOAppDefault, ZLayer}

/** Run all samples as ZIO tests */
object SamplesSpec extends ZIOSpecDefault {

  private val ignoredAppArgs: ULayer[ZIOAppArgs] = ZLayer.succeed(ZIOAppArgs.apply(Chunk.empty))

  override def spec =
    suite("SamplesSpec")(
      testApp("Doobie", doobie.LayeredApp),
      testApp("Doobie-Streaming", doobie.LayeredAppStreaming)
    )

  private def testApp(name: String, app: ZIOAppDefault) =
    test(s"$name LayeredApp prints its progress then the trio") {
      for {
        _      <- app.run.provide(ignoredAppArgs ++ Scope.default)
        output <- TestConsole.output
      } yield assertTrue(
        output == Vector(
          "Starting the app\n",
          "Creating the table\n",
          "Inserting the trio\n",
          "Reading the trio\n",
          "Buffy Summers, Willow Rosenberg, Alexander Harris\n"
        )
      )
    }

}
