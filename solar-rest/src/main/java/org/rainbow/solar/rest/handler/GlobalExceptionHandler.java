/**
 *
 */
package org.rainbow.solar.rest.handler;

import org.rainbow.solar.rest.err.HourlyElectricityPanelMismatchError;
import org.rainbow.solar.rest.err.HourlyElectricityReadingDateRequiredError;
import org.rainbow.solar.rest.err.HourlyElectricityReadingRequiredError;
import org.rainbow.solar.rest.err.PanelSerialDuplicateError;
import org.rainbow.solar.rest.err.PanelSerialMaxLengthExceededError;
import org.rainbow.solar.rest.err.PanelSerialRequiredError;
import org.rainbow.solar.rest.err.SolarError;
import org.rainbow.solar.rest.err.SolarErrorCode;
import org.rainbow.solar.service.exc.HourlyElectricityPanelMismatchException;
import org.rainbow.solar.service.exc.HourlyElectricityReadingDateRequiredException;
import org.rainbow.solar.service.exc.HourlyElectricityReadingRequiredException;
import org.rainbow.solar.service.exc.PanelSerialDuplicateException;
import org.rainbow.solar.service.exc.PanelSerialMaxLengthExceededException;
import org.rainbow.solar.service.exc.PanelSerialRequiredException;
import org.rainbow.solar.service.util.ExceptionMessagesResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author biya-bi
 *
 */
@ControllerAdvice
@Component
public class GlobalExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler
	public ResponseEntity<SolarError> handle(Exception exception) {
		LOG.error(exception.getMessage(), exception);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new SolarError(SolarErrorCode.UNEXPECTED_ERROR.value(),
						ExceptionMessagesResourceBundle.getMessage("unexpected.error.has.occurred")));
	}

	@ExceptionHandler
	public ResponseEntity<PanelSerialDuplicateError> handle(PanelSerialDuplicateException exception) {
		LOG.error(exception.getMessage(), exception);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(new PanelSerialDuplicateError(exception.getSerial(), exception.getMessage()));
	}

	@ExceptionHandler
	public ResponseEntity<PanelSerialMaxLengthExceededError> handle(PanelSerialMaxLengthExceededException exception) {
		LOG.error(exception.getMessage(), exception);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new PanelSerialMaxLengthExceededError(
				exception.getSerial(), exception.getMaxLength(), exception.getMessage()));
	}

	@ExceptionHandler
	public ResponseEntity<PanelSerialRequiredError> handle(PanelSerialRequiredException exception) {
		LOG.error(exception.getMessage(), exception);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(new PanelSerialRequiredError(exception.getMessage()));
	}

	@ExceptionHandler
	public ResponseEntity<HourlyElectricityReadingDateRequiredError> handle(
			HourlyElectricityReadingDateRequiredException exception) {
		LOG.error(exception.getMessage(), exception);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(new HourlyElectricityReadingDateRequiredError(exception.getMessage()));
	}

	@ExceptionHandler
	public ResponseEntity<HourlyElectricityReadingRequiredError> handle(
			HourlyElectricityReadingRequiredException exception) {
		LOG.error(exception.getMessage(), exception);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(new HourlyElectricityReadingRequiredError(exception.getMessage()));
	}

	@ExceptionHandler
	public ResponseEntity<HourlyElectricityPanelMismatchError> handle(
			HourlyElectricityPanelMismatchException exception) {
		LOG.error(exception.getMessage(), exception);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new HourlyElectricityPanelMismatchError(
				exception.getHourlyElectricityId(), exception.getPanelId(), exception.getMessage()));
	}
}
