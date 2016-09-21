package infrastructure

import scala.scalajs.js
import js.Promise
import js.annotation._
import js.|

@js.native
trait FirebaseAuthResult extends js.Object {
  var auth: js.Any = js.native
  var expires: Double = js.native
}

@js.native
trait FirebaseDataSnapshot extends js.Object {
  def exists(): Boolean = js.native
  def `val`(): js.Dynamic = js.native
  def child(childPath: String): FirebaseDataSnapshot = js.native
  def forEach(childAction: js.Function1[FirebaseDataSnapshot, Boolean]): Boolean = js.native
  def hasChild(childPath: String): Boolean = js.native
  def hasChildren(): Boolean = js.native
  def key(): String = js.native
  def name(): String = js.native
  def numChildren(): Double = js.native
  def ref(): Firebase = js.native
  def getPriority(): js.Dynamic = js.native
  def exportVal(): Object = js.native
}

@js.native
trait FirebaseOnDisconnect extends js.Object {
  def set(value: js.Any, onComplete: js.Function1[js.Any, Unit]): Unit = js.native
  def set(value: js.Any): Promise[Unit] = js.native
  def setWithPriority(value: js.Any, priority: String | Double, onComplete: js.Function1[js.Any, Unit]): Unit = js.native
  def setWithPriority(value: js.Any, priority: String | Double): Promise[Unit] = js.native
  def update(value: Object, onComplete: js.Function1[js.Any, Unit]): Unit = js.native
  def update(value: Object): Promise[Unit] = js.native
  def remove(onComplete: js.Function1[js.Any, Unit]): Unit = js.native
  def remove(): Promise[Unit] = js.native
  def cancel(onComplete: js.Function1[js.Any, Unit]): Unit = js.native
  def cancel(): Promise[Unit] = js.native
}

@js.native
trait FirebaseQuery extends js.Object {
  def on(eventType: String, callback: js.Function2[FirebaseDataSnapshot, js.UndefOr[String], Unit], cancelCallback: js.Function1[js.Any, Unit] = ???, context: Object = ???): js.Function2[FirebaseDataSnapshot, js.UndefOr[String], Unit] = js.native
  def off(eventType: String = ???, callback: js.Function2[FirebaseDataSnapshot, js.UndefOr[String], Unit] = ???, context: Object = ???): Unit = js.native
  def once(eventType: String, successCallback: js.Function1[FirebaseDataSnapshot, Unit]): Unit = js.native
  def once(eventType: String, successCallback: js.Function1[FirebaseDataSnapshot, Unit], context: Object): Unit = js.native
  def once(eventType: String, successCallback: js.Function1[FirebaseDataSnapshot, Unit], failureCallback: js.Function1[js.Any, Unit], context: Object = ???): Unit = js.native
  def orderByChild(key: String): FirebaseQuery = js.native
  def orderByKey(): FirebaseQuery = js.native
  def orderByValue(): FirebaseQuery = js.native
  def orderByPriority(): FirebaseQuery = js.native
  def limit(limit: Double): FirebaseQuery = js.native
  def startAt(value: String | Double, key: String = ???): FirebaseQuery = js.native
  def endAt(value: String | Double, key: String = ???): FirebaseQuery = js.native
  def equalTo(value: String | Double | Boolean, key: String = ???): FirebaseQuery = js.native
  def limitToFirst(limit: Double): FirebaseQuery = js.native
  def limitToLast(limit: Double): FirebaseQuery = js.native
  def ref(): Firebase = js.native
}

@js.native
class Firebase(firebaseURL: String) extends FirebaseQuery {
  def auth(authToken: String, onComplete: js.Function2[js.Any, FirebaseAuthResult, Unit], onCancel: js.Function1[js.Any, Unit] = ???): Unit = js.native
  def auth(authToken: String): Promise[FirebaseAuthResult] = js.native
  def authWithCustomToken(autoToken: String): Promise[FirebaseAuthData] = js.native
  def authWithCustomToken(autoToken: String, options: Object): Promise[FirebaseAuthData] = js.native
  def authWithCustomToken(autoToken: String, onComplete: js.Function2[js.Any, FirebaseAuthData, Unit], options: Object = ???): Unit = js.native
  def authAnonymously(): Promise[FirebaseAuthData] = js.native
  def authAnonymously(options: Object): Promise[FirebaseAuthData] = js.native
  def authAnonymously(onComplete: js.Function2[js.Any, FirebaseAuthData, Unit], options: Object = ???): Unit = js.native
  def authWithPassword(credentials: FirebaseCredentials): Promise[FirebaseAuthData] = js.native
  def authWithPassword(credentials: FirebaseCredentials, options: Object): Promise[FirebaseAuthData] = js.native
  def authWithPassword(credentials: FirebaseCredentials, onComplete: js.Function2[js.Any, FirebaseAuthData, Unit], options: Object = ???): Unit = js.native
  def authWithOAuthPopup(provider: String): Promise[FirebaseAuthData] = js.native
  def authWithOAuthPopup(provider: String, options: Object): Promise[FirebaseAuthData] = js.native
  def authWithOAuthPopup(provider: String, onComplete: js.Function2[js.Any, FirebaseAuthData, Unit], options: Object = ???): Unit = js.native
  def authWithOAuthRedirect(provider: String): Promise[Unit] = js.native
  def authWithOAuthRedirect(provider: String, options: Object): Promise[Unit] = js.native
  def authWithOAuthRedirect(provider: String, onComplete: js.Function1[js.Any, Unit], options: Object = ???): Unit = js.native
  def authWithOAuthToken(provider: String, credentials: String | Object): Promise[FirebaseAuthData] = js.native
  def authWithOAuthToken(provider: String, credentials: String | Object, options: Object): Promise[FirebaseAuthData] = js.native
  def authWithOAuthToken(provider: String, credentials: String | Object, onComplete: js.Function2[js.Any, FirebaseAuthData, Unit], options: Object = ???): Unit = js.native
  def getAuth(): FirebaseAuthData = js.native
  def onAuth(onComplete: js.Function1[FirebaseAuthData, Unit], context: Object = ???): Unit = js.native
  def offAuth(onComplete: js.Function1[FirebaseAuthData, Unit], context: Object = ???): Unit = js.native
  def unauth(): Unit = js.native
  def child(childPath: String): Firebase = js.native
  def parent(): Firebase = js.native
  def root(): Firebase = js.native
  def key(): String = js.native
  def name(): String = js.native
  override def toString(): String = js.native
  def set(value: js.Any, onComplete: js.Function1[js.Any, Unit]): Unit = js.native
  def set(value: js.Any): Promise[Unit] = js.native
  def update(value: Object, onComplete: js.Function1[js.Any, Unit]): Unit = js.native
  def update(value: Object): Promise[Unit] = js.native
  def remove(onComplete: js.Function1[js.Any, Unit]): Unit = js.native
  def remove(): Promise[Unit] = js.native
  def push(value: js.Any = ???, onComplete: js.Function1[js.Any, Unit] = ???): Firebase = js.native
  def setWithPriority(value: js.Any, priority: String | Double, onComplete: js.Function1[js.Any, Unit]): Unit = js.native
  def setWithPriority(value: js.Any, priority: String | Double): Promise[Unit] = js.native
  def setPriority(priority: String | Double, onComplete: js.Function1[js.Any, Unit]): Unit = js.native
  def setPriority(priority: String | Double): Promise[Unit] = js.native
  def transaction(updateFunction: js.Function1[js.Any, Any], onComplete: js.Function3[js.Any, Boolean, FirebaseDataSnapshot, Unit] = ???, applyLocally: Boolean = ???): Unit = js.native
  def createUser(credentials: FirebaseCredentials, onComplete: js.Function2[js.Any, js.Any, Unit]): Unit = js.native
  def changeEmail(credentials: FirebaseChangeEmailCredentials, onComplete: js.Function1[js.Any, Unit]): Unit = js.native
  def changePassword(credentials: FirebaseChangePasswordCredentials, onComplete: js.Function1[js.Any, Unit]): Unit = js.native
  def removeUser(credentials: FirebaseCredentials, onComplete: js.Function1[js.Any, Unit]): Unit = js.native
  def resetPassword(credentials: FirebaseResetPasswordCredentials, onComplete: js.Function1[js.Any, Unit]): Unit = js.native
  def onDisconnect(): FirebaseOnDisconnect = js.native
}

@js.native
trait FirebaseStatic extends js.Object {
  /* ??? ConstructorMember(FunSignature(List(),List(FunParam(Ident(firebaseURL),false,Some(TypeRef(CoreType(string),List())))),Some(TypeRef(TypeName(Firebase),List())))) */
  def goOffline(): Unit = js.native
  def goOnline(): Unit = js.native
  var ServerValue: { def TIMESTAMP: js.Any; } = js.native
}

@js.native
trait FirebaseAuthData extends js.Object {
  var uid: String = js.native
  var provider: String = js.native
  var token: String = js.native
  var expires: Double = js.native
  var auth: Object = js.native
  var google: FirebaseAuthDataGoogle = js.native
}

@js.native
trait FirebaseAuthDataGoogle extends js.Object {
  var accessToken: String = js.native
  var cachedUserProfile: FirebaseAuthDataGoogleCachedUserProfile = js.native
  var displayName: String = js.native
  var email: String = js.native
  var id: String = js.native
  var profileImageURL: String = js.native
}

@js.native
trait FirebaseAuthDataGoogleCachedUserProfile extends js.Object {
  var `family name`: String = js.native
  var gender: String = js.native
  var `given name`: String = js.native
  var id: String = js.native
  var link: String = js.native
  var locale: String = js.native
  var name: String = js.native
  var picture: String = js.native
}

@js.native
trait FirebaseCredentials extends js.Object {
  var email: String = js.native
  var password: String = js.native
}

@js.native
trait FirebaseChangePasswordCredentials extends js.Object {
  var email: String = js.native
  var oldPassword: String = js.native
  var newPassword: String = js.native
}

@js.native
trait FirebaseChangeEmailCredentials extends js.Object {
  var oldEmail: String = js.native
  var newEmail: String = js.native
  var password: String = js.native
}

@js.native
trait FirebaseResetPasswordCredentials extends js.Object {
  var email: String = js.native
}

@js.native
object Importedjs extends js.GlobalScope {
  var Firebase: FirebaseStatic = js.native
}