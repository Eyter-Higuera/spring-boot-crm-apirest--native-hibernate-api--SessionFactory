package com.eyterhiguera.spring.apirest.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eyterhiguera.spring.apirest.entity.Customer;
import com.eyterhiguera.spring.apirest.service.CustomerService;

@RestController
@RequestMapping("/api")
public class CustomerRestController {
   
	//autowired the CustomerService
	@Autowired
	private CustomerService customerService;
	
	//add mapping for GET/customers
	@GetMapping("/customers")
	public List<Customer> getCustomers(){
		 
		return customerService.getCustomers();
	}
	
	//add mapping for GET/customers/{customerId}
	@GetMapping("/customers/{customerId}")
	public Customer getCustomer(@PathVariable int customerId) {
	
		//if customer id is not found in database, returns null
		//we want a 404..if customer not found
		//we need to refactor our code, if theCustomer is null, then throw exception
		Customer theCustumer= customerService.getCustomer(customerId);

/*Si no existe un registro 404 o si es un bad_request 400 y no se maneja 
 * una exception entonces enviaría un cuerpo de json vacío */
/*si el customer no existe en la bd entonces arroja una exception que lo enviaría 
 *al @ControllerAdvice y lo manejaría el @Exception handler y enviaría la respuesta en formato JSON
		*/
		if(theCustumer == null) {
		 
			throw new CustomerNotFoundException("Customer id not found -" + customerId);
		}
		
		//for null objects, jackson returns empty JSON body
		return theCustumer;
	}
	
	//add mapping for POST/customers - add  new customer
	//we use @RequestBody to access the request body as a POJO
	@PostMapping("/customers")
	public Customer addCustomer(@RequestBody Customer theCustomer) {
		
		//If REST client sending a request to "add", using HTTP POST 
		//then we ignore any id in the request
		//we overwrite the id 0, to effectively set it to null/empty
		//also just in case the pass an id in JSON.. set id to 0
		//this  is force a save of new item.. instead of update
		//if id is 0, then DAO will "INSERT" new customer
		theCustomer.setId(0);
		
		customerService.saveCustomer(theCustomer);
		
		return theCustomer;
	}
	
	//add mapping for PUT/customers - update existing customer
	//we use @RequestBody to access the request body as a POJO
	//the @RequestBody includes the id
	@PutMapping("/customers")
	public Customer updateCustomer(@RequestBody Customer theCustomer) {
		
		
		//Since customer ID is set, DAO will UPDATE customer in the database
		customerService.saveCustomer(theCustomer);
			
		return theCustomer;
	}
	
	//add mapping for DELETE/customers - delete customer
	@DeleteMapping("/customers/{customerId}")
	public String deleteCustomer(@PathVariable int customerId) {
	   
		Customer tempCustomer = customerService.getCustomer(customerId);
		
		//throw exception if null
		 if(tempCustomer==null) {
			
			 throw new CustomerNotFoundException("Customer not found - " + customerId);
		 }
		 
		 customerService.deleteCustomer(customerId);
		 
		return "Delete customer id - " + customerId;
	}
}
