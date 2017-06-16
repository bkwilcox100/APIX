package com.heb.liquidsky.spring.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heb.liquidsky.common.HebEnvironmentProperties;

@RestController
@RequestMapping(value="/echo/v1")
public class EchoController {
	
	@PostMapping(value="/echo")
	public Map<String, Object> echo(@RequestBody Map<String, Object> body, HttpServletRequest request) {
		return this.testEcho(request, body);
	}
	
	/**
	 * Echos back data sent to it including body payload and all header key value pairs
	 * EXAMPLE CALL:  POST http://localhost:8080/echo/v1/echo
	 * 
	 * @param	request	The HttpServletRequest used to get cookies or header keys
	 * @param	requestBodyMap	a map containing any data sent in the body of the post
	 *  
	 * @return	
	 */
	public Map<String, Object> testEcho(
			HttpServletRequest request, 
			Map<String, Object> requestBodyMap) {
		Map<String, Object> returnValue = new HashMap<String, Object>();
		Map<String, Object> headerValues = new HashMap<String, Object>();
		
		returnValue.put("requestBody", requestBodyMap);
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()){
			String elementName = headerNames.nextElement();
			headerValues.put(elementName, request.getHeader(elementName));
		}
		returnValue.put("Header", headerValues);

		// get some GAE information
		returnValue.put("GAE InstanceId", HebEnvironmentProperties.getInstance().getInstanceId());
		return returnValue;
	}
	
}
