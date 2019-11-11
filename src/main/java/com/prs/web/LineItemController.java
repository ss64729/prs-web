package com.prs.web;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.prs.business.LineItem;
import com.prs.business.Request;
import com.prs.db.LineItemRepository;
import com.prs.db.RequestRepository;

@CrossOrigin
@RestController
@RequestMapping("/line-items")
public class LineItemController {
	@Autowired
	LineItemRepository lineItemRepo;
	@Autowired
	RequestRepository requestRepo;

	// Return all lineItem with logging
	@GetMapping("/")
	public JsonResponse listLineItem() {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(lineItemRepo.findAll());

		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	@GetMapping("/{id}")
	public JsonResponse getLineItem(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(lineItemRepo.findById(id));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}

		return jr;
	}

	// select * from lineitem where requestid = ?
	@GetMapping("/lines-for-pr/{id}")
	public JsonResponse getLinesForRequest(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(lineItemRepo.findAllByRequestId(id));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	@PostMapping("/")
	public JsonResponse addLineItem(@RequestBody LineItem l) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(lineItemRepo.save(l));
			recalculateTotal(l.getRequest().getId());
			// recalculateTotal(l.getRequest());
			// getTotal(l.getRequest().getId());
		} catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getInstance(dive.getRootCause().getMessage());
			dive.printStackTrace();
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}

		return jr;
	}

	@PutMapping("/")
	public JsonResponse updateLineItem(@RequestBody LineItem l) {
		JsonResponse jr = null;
		try {
			if (lineItemRepo.existsById(l.getId())) {
				jr = JsonResponse.getInstance(lineItemRepo.save(l));
				recalculateTotal(l.getRequest().getId());
			} else {
				jr = JsonResponse.getInstance("error updating lineItem id: " + l.getId() + " doesn't exist");
			}
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	@DeleteMapping("/{id}")
	public JsonResponse deleteLineItem(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			if (lineItemRepo.existsById(id)) {
				Optional<LineItem> l = lineItemRepo.findById(id);
				int r = l.get().getRequest().getId();
				lineItemRepo.deleteById(id);
				jr = JsonResponse.getInstance("Delete successful");
				recalculateTotal(r);
			} else {
				jr = JsonResponse.getInstance("error deleting lineItem id: " + id + " doesn't exist");
			}
		} catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getInstance(dive.getRootCause().getMessage());
			dive.printStackTrace();

		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}

		return jr;
	}

	private void recalculateTotal(int requestID) {
		// get a list of items for a specific request

		List<LineItem> list = lineItemRepo.findAllByRequestId(requestID);
		// loop thru list to sum a total
		double total = 0.0;
		for (LineItem l : list) {
			double sum = l.getProduct().getPrice() * l.getQuantity();
			total += sum;
		}
		// save that total in the Request instance
		Request r = requestRepo.findById(requestID).get();
		r.setTotal(total);
		try {
			requestRepo.save(r);
		} catch (Exception e) {
			throw e;
		}

	}
}
