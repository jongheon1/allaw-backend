package site.allawbackend.opinion.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import site.allawbackend.common.exception.BillMismatchException;
import site.allawbackend.common.exception.BillNotFoundException;
import site.allawbackend.common.exception.UnauthorizedUserException;
import site.allawbackend.common.exception.UserNotFoundException;
import site.allawbackend.entity.Bill;
import site.allawbackend.entity.Role;
import site.allawbackend.entity.User;
import site.allawbackend.opinion.dto.OpinionRequest;
import site.allawbackend.opinion.dto.OpinionResponse;
import site.allawbackend.opinion.entity.Opinion;
import site.allawbackend.opinion.repository.OpinionRepository;
import site.allawbackend.repository.BillRepository;
import site.allawbackend.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class OpinionServiceTest {

    @Autowired
    private OpinionService opinionService;
    
    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private UserRepository userRepository;

    User user1;
    User user2;

    Bill bill1;
    Bill bill2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .name("Test User 1")
                .email("testuser1@example.com")
                .role(Role.USER)
                .build();
        user2 = User.builder()
                .name("Test User 2")
                .email("testuser2@example.com")
                .role(Role.USER)
                .build();

        userRepository.saveAll(List.of(user1, user2));

        bill1 = new Bill(null, 1, "Test Bill 1", "Test Proposer 1", "2024-01-01", "http://example.com/1");
        bill2 = new Bill(null, 2, "Test Bill 2", "Test Proposer 2", "2024-01-02", "http://example.com/2");

        billRepository.saveAll(List.of(bill1, bill2));
    }

    @Test
    @DisplayName("새 의견을 생성합니다.")
    void createOpinion() {
        OpinionRequest opinionRequest = createOpinionRequest(1L, "Test Opinion 1", 3);

        Long opinionId = opinionService.createOpinion(user1.getId(), opinionRequest);

        Opinion opinion = opinionRepository.findById(opinionId).get();
        assertThat(opinionId).isEqualTo(opinion.getId());
    }

    @Test
    @DisplayName("존재하지 않는 사용자는 의견을 생성할 수 없습니다.")
    void cantCreateOpinionIfThereIsNoUser() {
        OpinionRequest opinionRequest = createOpinionRequest(1L, "Test Opinion 1", 3);

        assertThatThrownBy(() -> opinionService.createOpinion(100L, opinionRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("존재하지 않는 사용자입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 법안에는 의견을 생성할 수 없습니다.")
    void cantCreateOpinionIfThereIsNoBill() {
        OpinionRequest opinionRequest = createOpinionRequest(3000L, "Test Opinion 1", 3);

        assertThatThrownBy(() -> opinionService.createOpinion(1L, opinionRequest))
                .isInstanceOf(BillNotFoundException.class)
                .hasMessage("존재하지 않는 법안입니다.");
    }

    @Test
    @DisplayName("특정 법안에 대한 의견을 조회합니다.")
    void getOpinionByBill() {
        OpinionRequest opinionRequest1 = createOpinionRequest(1L, "Test Opinion 1", 3);
        OpinionRequest opinionRequest2 = createOpinionRequest(1L, "Test Opinion 2", 5);

        Long opinionId1 = opinionService.createOpinion(user1.getId(), opinionRequest1);
        Long opinionId2 = opinionService.createOpinion(user2.getId(), opinionRequest2);

        List<OpinionResponse> opinionsByBillId = opinionService.getOpinionsByBillId(bill1.getId());

        assertThat(opinionsByBillId)
                .hasSize(2)
                .extracting("detail", "grade")
                .contains(
                        tuple("Test Opinion 1", 3),
                        tuple("Test Opinion 2", 5)
                );
    }

    @Test
    @DisplayName("존재하지 않는 법안에 대한 의견은 조회할 수 없습니다")
    void canGetOpinionByBill() {
        assertThatThrownBy(()-> opinionService.getOpinionsByBillId(300L))
                .isInstanceOf(BillNotFoundException.class)
                .hasMessage("존재하지 않는 법안입니다.");
    }

    @Test
    @DisplayName("의견을 수정합니다.")
    void updateOpinion() {
        OpinionRequest opinionRequest = createOpinionRequest(bill1.getId(), "Test Opinion 1", 3);
        Long opinionId = opinionService.createOpinion(user1.getId(), opinionRequest);

        OpinionRequest updatedRequest = new OpinionRequest(bill1.getId(), "Updated opinion", 4);
        OpinionResponse opinionResponse = opinionService.updateOpinion(user1.getId(), opinionId, updatedRequest);

        assertThat(opinionResponse)
                .extracting("detail", "grade")
                .contains("Updated opinion", 4);
    }

    @Test
    @DisplayName("다른 사용자의 의견은 수정할 수 없습니다.")
    void cantUpdateOtherUsersOpinion() {
        OpinionRequest opinionRequest = createOpinionRequest(bill1.getId(), "Test Opinion 1", 3);
        Long opinionId = opinionService.createOpinion(user1.getId(), opinionRequest);

        OpinionRequest updatedRequest = new OpinionRequest(bill1.getId(), "Updated opinion", 4);

        assertThatThrownBy(() -> opinionService.updateOpinion(user2.getId(), opinionId, updatedRequest))
                .isInstanceOf(UnauthorizedUserException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    @Test
    @DisplayName("다른 법안의 의견은 수정할 수 없습니다.")
    void cantUpdateOtherBillsOpinion() {
        OpinionRequest opinionRequest = createOpinionRequest(bill1.getId(), "Test Opinion 1", 3);
        Long opinionId = opinionService.createOpinion(user1.getId(), opinionRequest);

        OpinionRequest updatedRequest = new OpinionRequest(bill2.getId(), "Updated opinion", 4);

        assertThatThrownBy(() -> opinionService.updateOpinion(user1.getId(), opinionId, updatedRequest))
                .isInstanceOf(BillMismatchException.class)
                .hasMessage("법안이 일치하지 않습니다.");
    }

    @Test
    @DisplayName("의견을 삭제합니다.")
    void deleteOpinion() {
        OpinionRequest opinionRequest1 = createOpinionRequest(bill1.getId(), "Test Opinion 1", 3);
        OpinionRequest opinionRequest2 = createOpinionRequest(bill1.getId(), "Test Opinion 2", 5);
        Long opinionId1 = opinionService.createOpinion(user1.getId(), opinionRequest1);
        Long opinionId2 = opinionService.createOpinion(user2.getId(), opinionRequest2);

        opinionService.deleteOpinion(user1.getId(), opinionId1);

        List<OpinionResponse> opinionsByBillId = opinionService.getOpinionsByBillId(bill1.getId());

        assertThat(opinionsByBillId)
                .hasSize(1)
                .extracting("detail", "grade")
                .containsOnly(
                        tuple("Test Opinion 2", 5)
                );
    }

    @Test
    @DisplayName("존재하지 않는 의견 삭제할 수 없습니다.")
    void cantDeleteNonExistingOpinion() {
        OpinionRequest opinionRequest1 = createOpinionRequest(bill1.getId(), "Test Opinion 1", 3);
        Long opinionId1 = opinionService.createOpinion(user1.getId(), opinionRequest1);

        assertThatThrownBy(()-> opinionService.deleteOpinion(user1.getId(), 4L))
                .isInstanceOf(OpinionNotFoundException.class)
                .hasMessage("존재하지 않는 의견입니다.");
    }
    @Test
    @DisplayName("다른 사용자의 의견은 삭제할 수 없습니다.")
    void cantDeleteOtherUsersOpinion() {
        OpinionRequest opinionRequest = createOpinionRequest(bill1.getId(), "Test Opinion 1", 3);
        Long opinionId = opinionService.createOpinion(user1.getId(), opinionRequest);

        assertThatThrownBy(() -> opinionService.deleteOpinion(user2.getId(), opinionId))
                .isInstanceOf(UnauthorizedUserException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    private OpinionRequest createOpinionRequest(Long billId, String detail, int grade) {
        return new OpinionRequest(billId, detail, grade);
    }

}