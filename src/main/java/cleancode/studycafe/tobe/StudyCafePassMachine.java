package cleancode.studycafe.tobe;

import cleancode.studycafe.tobe.exception.AppException;
import cleancode.studycafe.tobe.io.InputHandler;
import cleancode.studycafe.tobe.io.OutputHandler;
import cleancode.studycafe.tobe.io.StudyCafeFileHandler;
import cleancode.studycafe.tobe.model.StudyCafeLockerPass;
import cleancode.studycafe.tobe.model.StudyCafePass;
import cleancode.studycafe.tobe.model.StudyCafePassType;

import java.util.List;
import java.util.Optional;

public class StudyCafePassMachine {

    private final InputHandler inputHandler = new InputHandler();
    private final OutputHandler outputHandler = new OutputHandler();
    private final StudyCafeFileHandler studyCafeFileHandler = new StudyCafeFileHandler();

    public void run() {
        try {
            outputHandler.showWelcomeMessage();
            outputHandler.showAnnouncement();

            StudyCafePass selectedPass = selectPass();
            Optional<StudyCafeLockerPass> optionalLockerPass = optionalLockerPass(selectedPass);

            optionalLockerPass.ifPresentOrElse(
              lockerPass -> outputHandler.showPassOrderSummary(selectedPass, lockerPass),
              ()-> outputHandler.showPassOrderSummary(selectedPass)
            );
            if (optionalLockerPass.isPresent()) {
                outputHandler.showPassOrderSummary(selectedPass, optionalLockerPass.get());
            } else {
                outputHandler.showPassOrderSummary(selectedPass, null);
            }
        } catch (AppException e) {
            outputHandler.showSimpleMessage(e.getMessage());
        } catch (Exception e) {
            outputHandler.showSimpleMessage("알 수 없는 오류가 발생했습니다.");
        }
    }

    private List<StudyCafePass> findPassCandidatesBy(StudyCafePassType studyCafePassType) {      
      List<StudyCafePass> allPasses = studyCafeFileHandler.readStudyCafePasses();
      return allPasses.stream()
          .filter(studyCafePass -> studyCafePass.getPassType() == studyCafePassType)
          .toList();
    }

    private StudyCafePass selectPass(){
      outputHandler.askPassTypeSelection();
      StudyCafePassType passType = inputHandler.getPassTypeSelectingUserAction();

      List<StudyCafePass> passCandidates = findPassCandidatesBy(passType);

      outputHandler.showPassListForSelection(passCandidates);
      return inputHandler.getSelectPass(passCandidates);
    }

    private Optional<StudyCafeLockerPass> optionalLockerPass(StudyCafePass selectedPass) {
      if(selectedPass.getPassType() != StudyCafePassType.FIXED) {
          return Optional.empty();
      }
      StudyCafeLockerPass lockerPassCandidate = findLockerPassCandidateBy(selectedPass);
      
      if (lockerPassCandidate != null) {
          outputHandler.askLockerPass(lockerPassCandidate);
          boolean isLockerSelection = inputHandler.getLockerSelection();

          if (isLockerSelection) {
              return Optional.of(lockerPassCandidate);
          }
      }
      return Optional.empty();
    }

    private StudyCafeLockerPass findLockerPassCandidateBy(StudyCafePass pass) {
      List<StudyCafeLockerPass> allLockerPasses = studyCafeFileHandler.readLockerPasses();
      
      return allLockerPasses.stream()
          .filter(lockerPass ->
              lockerPass.getPassType() == pass.getPassType()
                  && lockerPass.getDuration() == pass.getDuration()
          )
          .findFirst()
          .orElse(null);
    }
}
