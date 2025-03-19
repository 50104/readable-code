package cleancode.studycafe.tobe;

import cleancode.studycafe.tobe.exception.AppException;
import cleancode.studycafe.tobe.io.StudyCafeIOHandler;
import cleancode.studycafe.tobe.model.order.StudyCafePassOrder;
import cleancode.studycafe.tobe.model.pass.StudyCafePassType;
import cleancode.studycafe.tobe.model.pass.StudyCafeSeatPass;
import cleancode.studycafe.tobe.model.pass.StudyCafeSeatPasses;
import cleancode.studycafe.tobe.model.pass.locker.StudyCafeLockerPass;
import cleancode.studycafe.tobe.model.pass.locker.StudyCafeLockerPasses;
import cleancode.studycafe.tobe.provider.LockerPassProvider;
import cleancode.studycafe.tobe.provider.SeatPassProvider;

import java.util.List;
import java.util.Optional;

public class StudyCafePassMachine {

    private final StudyCafeIOHandler ioHandler = new StudyCafeIOHandler();
    private final SeatPassProvider seatPassProvider;
    private final LockerPassProvider lockerPassProvider;

    public StudyCafePassMachine(SeatPassProvider seatPassProvider, LockerPassProvider lockerPassProvider) {
        this.seatPassProvider = seatPassProvider;
        this.lockerPassProvider = lockerPassProvider;
    }

    // 헥사고날 아키텍처 - 포트와 어댑터

    public void run() {
        try {
            ioHandler.showWelcomeMessage();
            ioHandler.showAnnouncement();

            StudyCafeSeatPass selectedPass = selectPass();
            Optional<StudyCafeLockerPass> optionalLockerPass = selectLockerPass(selectedPass);
            StudyCafePassOrder passOrder = StudyCafePassOrder.of(
              selectedPass, optionalLockerPass.orElse(null)
            );

            ioHandler.showPassOrderSummary(passOrder);
        } catch (AppException e) {
            ioHandler.showSimpleMessage(e.getMessage());
        } catch (Exception e) {
            ioHandler.showSimpleMessage("알 수 없는 오류가 발생했습니다.");
        }
    }

    private List<StudyCafeSeatPass> findPassCandidatesBy(StudyCafePassType studyCafePassType) { 
      // 1. 어떤 데이터를 필요로 하는가
      // 2. 데이터를 어디로부터 어떻게 가져올 것인가     
      StudyCafeSeatPasses allPasses = seatPassProvider.getSeatPasses();
      return allPasses.findPassBy(studyCafePassType);
    }

    private StudyCafeSeatPass selectPass(){
      StudyCafePassType passType = ioHandler.askPassTypeSelecting();
      List<StudyCafeSeatPass> passCandidates = findPassCandidatesBy(passType);

      return ioHandler.askPassTypeSelecting(passCandidates);
    }

    private Optional<StudyCafeLockerPass> selectLockerPass(StudyCafeSeatPass selectedPass) {
      if(selectedPass.cannotUseLocker()) {
          return Optional.empty();
      }
      Optional<StudyCafeLockerPass> lockerPassCandidate = findLockerPassCandidateBy(selectedPass);
      
      if (lockerPassCandidate.isPresent()) {
          StudyCafeLockerPass lockerPass = lockerPassCandidate.get();

          boolean isLockerSelected = ioHandler.askLockerPass(lockerPass);
          if (isLockerSelected) {
              return Optional.of(lockerPass);
          }
      }
      return Optional.empty();
    }

    private Optional<StudyCafeLockerPass> findLockerPassCandidateBy(StudyCafeSeatPass pass) {
      StudyCafeLockerPasses allLockerPasses = lockerPassProvider.getLockerPasses();
      
      return allLockerPasses.findLockerPassBy(pass);
    }
}
