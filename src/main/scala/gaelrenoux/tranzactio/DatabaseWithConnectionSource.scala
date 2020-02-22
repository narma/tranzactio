package gaelrenoux.tranzactio

import java.sql.{Connection => JavaSqlConnection}

import zio.ZIO
import zio.blocking.Blocking
import zio.clock.Clock
import zio.macros.delegate.Mix


/** Template implementing a default transactional mechanism, based on a ConnectionSource. */
private[tranzactio] trait DatabaseWithConnectionSource[Connection]
  extends DatabaseApi[Connection] with Blocking with Clock with ConnectionSource {

  import connectionSource._

  /** Base service in the template. */
  trait ServiceWithConnectionSource extends DatabaseApi.DatabaseServiceApi[Any, Connection] {

    def connectionFromSql(connection: JavaSqlConnection): ZIO[Any, Nothing, Connection]

    override def transactionR[R1, E, A](zio: ZIO[R1 with Connection, E, A])(implicit ev: R1 Mix Connection): ZIO[R1, Either[DbException, E], A] =
      for {
        r1 <- ZIO.environment[R1]
        a <- openConnection.bracket(closeConnection) { c: JavaSqlConnection =>
          setAutoCommit(c, autoCommit = false)
            .as(c)
            .flatMap(connectionFromSql)
            .map(ev.mix(r1, _))
            .flatMap(zio.mapError(Right(_)).provide(_))
            .tapBoth(
              _ => rollbackConnection(c),
              _ => commitConnection(c)
            )
        }
      } yield a

    override def autoCommitR[R1, E, A](zio: ZIO[R1 with Connection, E, A])(implicit ev: R1 Mix Connection): ZIO[R1, Either[DbException, E], A] =
      for {
        r1 <- ZIO.environment[R1]
        a <- openConnection.bracket(closeConnection) { c: JavaSqlConnection =>
          setAutoCommit(c, autoCommit = true)
            .as(c)
            .flatMap(connectionFromSql)
            .map(ev.mix(r1, _))
            .flatMap(zio.mapError(Right(_)).provide(_))
        }
      } yield a
  }

}
