package com.bbva.rbvd.dto.insurancemissionsale.dto;

import java.io.Serializable;

/**
 * The ResponseLibrary class...
 */
public class ResponseLibrary<T> implements Serializable  {
	private static final long serialVersionUID = 2931699728946643245L;
	private String statusProcess;
	private String flowProcess;
	private transient T body;

	private ResponseLibrary(String statusProcess, T body,String flowProcess) {
		this.statusProcess = statusProcess;
		this.body = body;
		this.flowProcess = flowProcess;
	}

	public T getBody() {
		return this.body;
	}


	public String getStatusProcess() {
		return this.statusProcess;
	}

	public String getFlowProcess() {
		return flowProcess;
	}

	public static final class ResponseServiceBuilder {
		private String status;
		private String flowProcess;

		public ResponseServiceBuilder() {
			//This method is an empty constructor
		}

		public static ResponseServiceBuilder an() {
			return new ResponseServiceBuilder();
		}

		public ResponseServiceBuilder statusIndicatorProcess(String statusIndicatorProcess) {
			this.status = statusIndicatorProcess;
			return this;
		}

		public ResponseServiceBuilder flowProcess(String flowProcess) {
			this.flowProcess = flowProcess;
			return this;
		}


		public <T> ResponseLibrary<T> body(T body) {
			return new ResponseLibrary<>(this.status, body,this.flowProcess);
		}

		public <T> ResponseLibrary<T> build() {
			return this.body(null);
		}
	}
}
