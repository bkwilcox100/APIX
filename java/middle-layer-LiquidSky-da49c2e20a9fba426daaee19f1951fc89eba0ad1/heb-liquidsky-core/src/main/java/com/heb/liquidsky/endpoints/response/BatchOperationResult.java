package com.heb.liquidsky.endpoints.response;

import java.util.ArrayList;
import java.util.List;

public class BatchOperationResult {

	private List<String> successes;
	private List<ServiceExceptionErrorItem> errors;

	public void addSuccess(String success) {
		if (this.successes == null) {
			this.successes = new ArrayList<>();
		}
		this.successes.add(success);
	}

	public void addError(ServiceExceptionErrorItem error) {
		if (this.errors == null) {
			this.errors = new ArrayList<>();
		}
		this.errors.add(error);
	}

	public <T> T generateResponse(T successResponse) throws ServiceException {
		if (this.errors != null && this.successes != null) {
			throw new PartialSuccessException("Some items were successfully processed", successes, errors);
		}
		if (this.errors != null && this.successes == null) {
			BadRequestException bre = new BadRequestException("No items were successfully processed");
			bre.setErrors(this.errors);
			throw bre;
		}
		return successResponse;
	}
}
