package cleancode.studycafe.tobe.model.pass.locker;

import java.util.List;
import java.util.Optional;

import cleancode.studycafe.tobe.model.pass.StudyCafeSeatPass;

public class StudyCafeLockerPasses {
  
  private final List<StudyCafeLockerPass> lockerPasses;

  private StudyCafeLockerPasses(List<StudyCafeLockerPass> lockerPasses) {
    this.lockerPasses = lockerPasses;
  }

  public static StudyCafeLockerPasses of(List<StudyCafeLockerPass> lockerPasses) {
    return new StudyCafeLockerPasses(lockerPasses);
  }

  public Optional<StudyCafeLockerPass> findLockerPassBy(StudyCafeSeatPass pass) {
    return lockerPasses.stream()
      .filter(pass::isSameDurationType)
      .findFirst();
  }
}
