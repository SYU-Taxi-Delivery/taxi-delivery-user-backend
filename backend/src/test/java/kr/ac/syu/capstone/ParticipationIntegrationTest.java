package kr.ac.syu.capstone;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test") @Import(TestClockConfiguration.class)
class ParticipationIntegrationTest extends ParticipationContract {}
