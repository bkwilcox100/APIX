package com.heb.liquidsky.spring.web;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heb.liquidsky.endpoints.{{interfaceName}};
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.endpoints.tools.EndpointUtils;

/**
 * Defines the endpoint servlet for Admin Rest API Discovery resources.
 *
 * @author {{Author}}
 */
