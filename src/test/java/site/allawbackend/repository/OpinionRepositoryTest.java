package site.allawbackend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import site.allawbackend.entity.Bill;
import site.allawbackend.opinion.entity.Opinion;
import site.allawbackend.entity.Role;
import site.allawbackend.entity.User;
import site.allawbackend.opinion.repository.OpinionRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class OpinionRepositoryTest {

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testFindByBill_Id() {
        User testUser1 = userRepository.save(User.builder()
                .name("Test User 1")
                .email("testuser1@example.com")
                .role(Role.USER)
                .build());

        User testUser2 = userRepository.save(User.builder()
                .name("Test User 2")
                .email("testuser2@example.com")
                .role(Role.USER)
                .build());

        Bill testBill1 = billRepository.save(new Bill(null, 123, "Test Bill 1", "Test Proposer 1", "2024-01-01", "http://example.com/1"));
        Bill testBill2 = billRepository.save(new Bill(null, 456, "Test Bill 2", "Test Proposer 2", "2024-01-02", "http://example.com/2"));

        opinionRepository.saveAll(List.of(
                new Opinion(testUser1, testBill1, "This is a test opinion 1", 5),
                new Opinion(testUser1, testBill2, "This is a test opinion 2", 4),
                new Opinion(testUser2, testBill1, "This is another test opinion 1", 3),
                new Opinion(testUser2, testBill2, "This is another test opinion 2", 2)
        ));

//        Optional<List<Opinion>> opinionsBill1 = opinionRepository.findByBill_Id(testBill1.getId());
//        Optional<List<Opinion>> opinionsBill2 = opinionRepository.findByBill_Id(testBill2.getId());
//
//        assertThat(opinionsBill1).isPresent();
//        assertThat(opinionsBill1.get()).hasSize(2);
//        assertThat(opinionsBill1.get().get(0).getDetail()).isEqualTo("This is a test opinion 1");
//
//        assertThat(opinionsBill2).isPresent();
//        assertThat(opinionsBill2.get()).hasSize(2);
//        assertThat(opinionsBill2.get().get(1).getDetail()).isEqualTo("This is another test opinion 2");
    }

}