package test

import controllers.ApplicationComponent
import models.{CrucibleWebServiceComponent, CrucibleComponent}
import org.specs2.mock.Mockito

trait TestApplication extends ApplicationComponent with Mockito
    with CrucibleComponent
    with CrucibleWebServiceComponent {
  override val crucibleWebService = mock[CrucibleWebService]
  override val crucible = mock[Crucible]
}
