package se.inera.intyg.intygmockservice.sendmessagetorecipient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.converter.SendMessageToRecipientConverter;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.dto.SendMessageToRecipientDTO;
import se.inera.intyg.intygmockservice.sendmessagetorecipient.repository.SendMessageToRecipientRepository;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToRecipient.v2.SendMessageToRecipientType;

@ExtendWith(MockitoExtension.class)
class SendMessageToRecipientControllerTest {

  @Mock private SendMessageToRecipientRepository repository;

  @Mock private SendMessageToRecipientConverter converter;

  @InjectMocks private SendMessageToRecipientController controller;

  @Test
  void getAllMessages_ShouldReturnAllMessages() {
    SendMessageToRecipientType message = new SendMessageToRecipientType();
    SendMessageToRecipientDTO messageDTO = new SendMessageToRecipientDTO();
    when(repository.findAll()).thenReturn(Arrays.asList(message));
    when(converter.convert(message)).thenReturn(messageDTO);

    List<SendMessageToRecipientDTO> result = controller.getAllMessages();

    assertThat(result).containsExactly(messageDTO);
  }

  @Test
  void deleteAllMessages_ShouldDeleteAllMessages() {
    controller.deleteAllMessages();

    verify(repository, times(1)).deleteAll();
  }

  @Test
  void getMessagesByRecipientId_ShouldReturnMessagesForRecipient() {
    String recipientId = "recipient1";
    SendMessageToRecipientType message = new SendMessageToRecipientType();
    SendMessageToRecipientDTO messageDTO = new SendMessageToRecipientDTO();
    when(repository.findByRecipientId(recipientId)).thenReturn(Arrays.asList(message));
    when(converter.convert(message)).thenReturn(messageDTO);

    List<SendMessageToRecipientDTO> result = controller.getMessagesByRecipientId(recipientId);

    assertThat(result).containsExactly(messageDTO);
  }
}
