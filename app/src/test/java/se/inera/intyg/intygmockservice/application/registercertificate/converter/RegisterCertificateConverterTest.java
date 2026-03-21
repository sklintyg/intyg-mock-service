package se.inera.intyg.intygmockservice.application.registercertificate.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.intygmockservice.application.common.converter.IntygConverter;
import se.inera.intyg.intygmockservice.application.common.dto.IntygDTO;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.v3.Intyg;
import se.riv.clinicalprocess.healthcond.certificate.v3.MeddelandeReferens;

@ExtendWith(MockitoExtension.class)
class RegisterCertificateConverterTest {

  @Mock private IntygConverter intygConverter;

  @InjectMocks private RegisterCertificateConverter converter;

  @Test
  void shouldDelegateIntygConversionToIntygConverter() {
    final var intyg = new Intyg();
    final var type = new RegisterCertificateType();
    type.setIntyg(intyg);
    when(intygConverter.convert(intyg)).thenReturn(IntygDTO.builder().build());

    converter.convert(type);

    verify(intygConverter).convert(intyg);
  }

  @Test
  void shouldSetIntygFromIntygConverter() {
    final var expected = IntygDTO.builder().build();
    final var type = new RegisterCertificateType();
    type.setIntyg(new Intyg());
    when(intygConverter.convert(any())).thenReturn(expected);

    final var result = converter.convert(type);

    assertEquals(expected, result.getIntyg());
  }

  @Test
  void shouldSetSvarPaToNullWhenAbsent() {
    final var type = new RegisterCertificateType();
    type.setIntyg(new Intyg());
    when(intygConverter.convert(any())).thenReturn(IntygDTO.builder().build());

    final var result = converter.convert(type);

    assertNull(result.getSvarPa());
  }

  @Test
  void shouldSetSvarPaMeddelandeIdWhenPresent() {
    final var svarPa = new MeddelandeReferens();
    svarPa.setMeddelandeId("msg-1");
    final var type = new RegisterCertificateType();
    type.setIntyg(new Intyg());
    type.setSvarPa(svarPa);
    when(intygConverter.convert(any())).thenReturn(IntygDTO.builder().build());

    final var result = converter.convert(type);

    assertNotNull(result.getSvarPa());
    assertEquals("msg-1", result.getSvarPa().getMeddelandeId());
  }

  @Test
  void shouldSetSvarPaReferensIdWhenPresent() {
    final var svarPa = new MeddelandeReferens();
    svarPa.setReferensId("ref-1");
    final var type = new RegisterCertificateType();
    type.setIntyg(new Intyg());
    type.setSvarPa(svarPa);
    when(intygConverter.convert(any())).thenReturn(IntygDTO.builder().build());

    final var result = converter.convert(type);

    assertEquals("ref-1", result.getSvarPa().getReferensId());
  }
}
