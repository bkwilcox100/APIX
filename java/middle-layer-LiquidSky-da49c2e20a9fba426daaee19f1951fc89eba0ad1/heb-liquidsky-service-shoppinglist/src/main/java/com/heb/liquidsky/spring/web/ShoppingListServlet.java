package com.heb.liquidsky.spring.web;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heb.liquidsky.endpoints.ShoppingListInterface;
import com.heb.liquidsky.endpoints.auth.User;
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.oauth.authenticator.GigyaAuthenticator;

@RestController
@RequestMapping(value="/_ah/api/shoppinglist/v1")
public class ShoppingListServlet {

	private static final Logger logger = Logger.getLogger(ShoppingListServlet.class.getName());
	private static final ShoppingListInterface SHOPPING_LIST_INTERFACE = new ShoppingListInterface();

	@DeleteMapping(value="/shoppingLists/{listId}")
	public Map<String, Object> deleteShoppingList(@PathVariable String listId, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.deleteShoppingList(request, listId, this.authenticate(request));
	}

	@DeleteMapping(value="/shoppingLists/{listId}/products/{itemId}")
	public Map<String, Object> deleteProductItem(@PathVariable String listId, @PathVariable String itemId, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.deleteProductItem(listId, itemId, request, this.authenticate(request));
	}

	@DeleteMapping(value="/shoppingLists/{listId}/coupons/{itemId}")
	public Map<String, Object> deleteCouponItem(@PathVariable String listId, @PathVariable String itemId, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.deleteCouponItem(listId, itemId, request, this.authenticate(request));
	}

	@DeleteMapping(value="/shoppingLists/{listId}/recipes/{itemId}")
	public Map<String, Object> deleteRecipeItem(@PathVariable String listId, @PathVariable String itemId, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.deleteRecipeItem(listId, itemId, request, this.authenticate(request));
	}

	@DeleteMapping(value="/shoppingLists/{listId}/freeforms/{itemId}")
	public Map<String, Object> deleteFreeformItem(@PathVariable String listId, @PathVariable String itemId, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.deleteFreeformItem(listId, itemId, request, this.authenticate(request));
	}

	@GetMapping(value="/shoppingLists")
	public Map<String, Object> getAllLists(HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.getAllLists(request, this.authenticate(request));
	}

	@GetMapping(value="/shoppingLists/{listId}")
	public Map<String, Object> readShoppingList(@PathVariable String listId, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.readShoppingList(request, listId, this.authenticate(request));
	}

	@GetMapping(value="/shoppingLists/{listId}/products")
	public Map<String, Object> readProductItems(@PathVariable String listId, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.readProductItems(listId, request, this.authenticate(request));
	}

	@GetMapping(value="/shoppingLists/{listId}/products/{itemId}")
	public Map<String, Object> readProductItem(@PathVariable String listId, @PathVariable String itemId, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.readProductItem(listId, itemId, request, this.authenticate(request));
	}

	@GetMapping(value="/shoppingLists/{listId}/coupons")
	public Map<String, Object> readCouponItems(@PathVariable String listId, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.readCouponItems(listId, request, this.authenticate(request));
	}

	@GetMapping(value="/shoppingLists/{listId}/coupons/{itemId}")
	public Map<String, Object> readCouponItem(@PathVariable String listId, @PathVariable String itemId, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.readCouponItem(listId, itemId, request, this.authenticate(request));
	}

	@GetMapping(value="/shoppingLists/{listId}/recipes")
	public Map<String, Object> readRecipeItems(@PathVariable String listId, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.readRecipeItems(listId, request, this.authenticate(request));
	}

	@GetMapping(value="/shoppingLists/{listId}/recipes/{itemId}")
	public Map<String, Object> readRecipeItem(@PathVariable String listId, @PathVariable String itemId, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.readRecipeItem(listId, itemId, request, this.authenticate(request));
	}

	@GetMapping(value="/shoppingLists/{listId}/freeforms")
	public Map<String, Object> readFreeformItems(@PathVariable String listId, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.readFreeformItems(listId, request, this.authenticate(request));
	}

	@GetMapping(value="/shoppingLists/{listId}/freeforms/{itemId}")
	public Map<String, Object> readFreeformItem(@PathVariable String listId, @PathVariable String itemId, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.readFreeformItem(listId, itemId, request, this.authenticate(request));
	}

	@PostMapping(value="/shoppingLists")
	public Map<String, Object> createShoppingList(@RequestParam(required=false, defaultValue="false") boolean singleList, @RequestBody Map<String, Object> body, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.createShoppingList(request, body, this.authenticate(request), singleList);
	}

	@PostMapping(value="/shoppingLists/{listId}/products")
	public Map<String, Object> createProductItems(@PathVariable String listId, @RequestBody Map<String, Object> body, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.createProductItems(listId, request, body, this.authenticate(request));
	}

	@PostMapping(value="/shoppingLists/{listId}/coupons")
	public Map<String, Object> createCouponItems(@PathVariable String listId, @RequestBody Map<String, Object> body, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.createCouponItems(listId, request, body, this.authenticate(request));
	}

	@PostMapping(value="/shoppingLists/{listId}/recipes")
	public Map<String, Object> createRecipeItems(@PathVariable String listId, @RequestBody Map<String, Object> body, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.createRecipeItems(listId, request, body, this.authenticate(request));
	}

	@PostMapping(value="/shoppingLists/{listId}/freeforms")
	public Map<String, Object> createFreeformItems(@PathVariable String listId, @RequestBody Map<String, Object> body, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.createFreeformItems(listId, request, body, this.authenticate(request));
	}

	@PutMapping(value="/shoppingLists/{listId}")
	public Map<String, Object> updateShoppingList(@PathVariable String listId, @RequestParam(required=false, defaultValue="false") boolean singleList, @RequestBody Map<String, Object> body, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.updateShoppingList(request, body, listId, singleList, this.authenticate(request));
	}

	@PutMapping(value="/shoppingLists/{listId}/products")
	public Map<String, Object> updateProductItems(@PathVariable String listId, @RequestBody Map<String, Object> body, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.updateProductItems(listId, request, body, this.authenticate(request));
	}

	@PutMapping(value="/shoppingLists/{listId}/products/{itemId}")
	public Map<String, Object> updateProductItem(@PathVariable String listId, @RequestBody Map<String, Object> body, @PathVariable String itemId, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.updateProductItem(listId, itemId, request, body, this.authenticate(request));
	}

	@PutMapping(value="/shoppingLists/{listId}/coupons")
	public Map<String, Object> updateCouponItems(@PathVariable String listId, @RequestBody Map<String, Object> body, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.updateCouponItems(listId, request, body, this.authenticate(request));
	}

	@PutMapping(value="/shoppingLists/{listId}/coupons/{itemId}")
	public Map<String, Object> updateCouponItem(@PathVariable String listId, @RequestBody Map<String, Object> body, @PathVariable String itemId, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.updateCouponItem(listId, itemId, request, body, this.authenticate(request));
	}

	@PutMapping(value="/shoppingLists/{listId}/recipes")
	public Map<String, Object> updateRecipeItems(@PathVariable String listId, @RequestBody Map<String, Object> body, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.updateRecipeItems(listId, request, body, this.authenticate(request));
	}

	@PutMapping(value="/shoppingLists/{listId}/recipes/{itemId}")
	public Map<String, Object> updateRecipeItem(@PathVariable String listId, @RequestBody Map<String, Object> body, @PathVariable String itemId, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.updateRecipeItem(listId, itemId, request, body, this.authenticate(request));
	}

	@PutMapping(value="/shoppingLists/{listId}/freeforms")
	public Map<String, Object> updateFreeformItems(@PathVariable String listId, @RequestBody Map<String, Object> body, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.updateFreeformItems(listId, request, body, this.authenticate(request));
	}

	@PutMapping(value="/shoppingLists/{listId}/freeforms/{itemId}")
	public Map<String, Object> updateFreeformItem(@PathVariable String listId, @RequestBody Map<String, Object> body, @PathVariable String itemId, HttpServletRequest request) throws ServiceException {
		return SHOPPING_LIST_INTERFACE.updateFreeformItem(listId, itemId, request, body, this.authenticate(request));
	}

	private User authenticate(HttpServletRequest request) {
		try {
			GigyaAuthenticator authenticator = new GigyaAuthenticator();
			return authenticator.authenticate(request);
		} catch (Throwable t) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Failure initializing Gigya", t);
			}
			return null;
		}
	}
}
