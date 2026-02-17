package com.pacifico.customer.validation;

import com.pacifico.customer.util.Messages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <b>Class</b>: ValidateTest <br/>
 * <b>Copyright</b>: 2024 Pacifico Seguros - Tribu Salsa <br/>.
 *
 * @author 2024  Pacifico Seguros - Tribu Salsa <br/>
 * <u>Service Provider</u>: SoaInt <br/>
 * <u>Developed by</u>: Technical Team <br/>
 * <u>Changes:</u><br/>
 * <ul>
 *   <li>
 *     February 16, 2026 Creación de Clase.
 *   </li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class ValidateTest {

  @InjectMocks
  private Validate validate;

  @ParameterizedTest
  @MethodSource("provideValidValues")
  void givenHeader_whenValidatePatterWithValidValues_thenErrorListIsEmpty(String value) {
    String nameField = "fieldName";
    List<String> errors = new ArrayList<>();

    validate.validatePatter(nameField, value, errors);

    assertTrue(errors.isEmpty(), "Errors list should be empty for valid value: " + value);
  }

  static Stream<String> provideValidValues() {
    return Stream.of(
        null,                                          // Null value
        "ABC",                                         // Only letters
        "12345",                                       // Only numbers
        "ABC123",                                      // Letters and numbers
        "ABC-123",                                     // With hyphen
        "ABC.123",                                     // With dot
        "ABC-123.DEF-456",                            // Multiple hyphens and dots
        "  ABC123  ",                                  // Leading and trailing spaces
        "abc",                                         // Lowercase letters
        "AbC",                                         // Mixed case letters
        "ABC123DEF456GHI789JKL012MNO345PQR678STU901VWX234YZ567", // Long valid string
        "ABC--123..DEF",                               // Consecutive hyphens and dots
        "A"                                            // Single character
    );
  }

  @ParameterizedTest
  @MethodSource("provideInvalidCharacters")
  void givenHeader_whenValidatePatterWithInvalidCharacters_thenErrorListContainsError(String value, String description) {
    String nameField = "fieldName";
    List<String> errors = new ArrayList<>();

    validate.validatePatter(nameField, value, errors);

    assertFalse(errors.isEmpty(), "Errors list should not be empty for value with " + description);
    assertEquals(1, errors.size(), "Errors list should contain exactly one error for " + description);
    assertTrue(errors.get(0).contains(nameField), "Error message should contain field name");
    assertTrue(errors.get(0).contains(Messages.HEADER_ONLY_LETTERS_NUMBER_HYPHEN), "Error message should contain validation message");
  }

  static Stream<Arguments> provideInvalidCharacters() {
    return Stream.of(
        Arguments.of("ABC@123", "special characters"),
        Arguments.of("ABC_123", "underscore"),
        Arguments.of("ABC 123", "space in middle"),
        Arguments.of("ABC/123", "slash"),
        Arguments.of("ABC(123)", "parentheses"),
        Arguments.of("ABC,123", "comma"),
        Arguments.of("ABC!123", "exclamation mark")
    );
  }

  @Test
  void givenHeader_whenValidatePatterWithDifferentFieldNames_thenErrorContainsCorrectFieldName() {
    String nameField1 = "field1";
    String nameField2 = "field2";
    List<String> errors = new ArrayList<>();

    validate.validatePatter(nameField1, "ABC@123", errors);
    validate.validatePatter(nameField2, "ABC$456", errors);

    assertEquals(2, errors.size(), "Errors list should contain exactly two errors");
    assertTrue(errors.get(0).startsWith(nameField1), "First error should contain first field name");
    assertTrue(errors.get(1).startsWith(nameField2), "Second error should contain second field name");
  }

  @Test
  void givenHeader_whenValidatePatterWithMultipleInvalidValues_thenErrorListAccumulatesErrors() {
    List<String> errors = new ArrayList<>();

    validate.validatePatter("field1", "ABC@123", errors);
    validate.validatePatter("field2", "XYZ$456", errors);
    validate.validatePatter("field3", "DEF%789", errors);

    assertEquals(3, errors.size(), "Errors list should accumulate all errors");
  }

  @Test
  void givenHeader_whenValidatePatterWithMixedValidAndInvalidValues_thenErrorListContainsOnlyInvalidErrors() {
    List<String> errors = new ArrayList<>();

    validate.validatePatter("field1", "ABC@123", errors);
    validate.validatePatter("field2", "XYZ456", errors);

    assertEquals(1, errors.size(), "Errors list should contain only one error for invalid value");
  }
}
