package com.prs.web;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.prs.business.Request;
import com.prs.db.RequestRepository;

@CrossOrigin
@RestController
@RequestMapping("/requests")
public class RequestController {
	@Autowired
	RequestRepository requestRepo;

	// Return all request with logging
	@GetMapping("/")
	public JsonResponse listRequest() {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(requestRepo.findAll());

		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	@GetMapping("/{id}")
	public JsonResponse getRequest(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(requestRepo.findById(id));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}

		return jr;
	}

	// select * from request where userid != ? and status ="review"
	// this will be the user id not the id for the request repo.
	@GetMapping("/list-review/{id}")
	public JsonResponse listReview(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(requestRepo.findByStatusAndUserIdNot("review", id));

		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}

		return jr;
	}

	@PostMapping("/")
	public JsonResponse addRequest(@RequestBody Request r) {
		JsonResponse jr = null;
		try {
			r.setSubmittedDate(LocalDate.now());
			r.setStatus("new");
			jr = JsonResponse.getInstance(requestRepo.save(r));
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
	public JsonResponse updateRequest(@RequestBody Request r) {
		JsonResponse jr = null;
		try {
			if (requestRepo.existsById(r.getId())) {
				jr = JsonResponse.getInstance(requestRepo.save(r));
			} else {
				jr = JsonResponse.getInstance("error updating request id: " + r.getId() + " doesn't exist");
			}
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}

		return jr;
	}

	@PutMapping("/submit-review")
	// update request set status = "review" , submittedDate = ? where id = ? and
	// Total > 50
	// update request set status = "approved", submittedDate = ? where id = ? and
	// Total <= 50
	public JsonResponse submitReviewRequest(@RequestBody Request r) {
		JsonResponse jr = null;
		try {
			if (requestRepo.existsById(r.getId())) {
				if (r.getTotal() > 50) {
					r.setStatus("review");
				} else {
					r.setStatus("approved");
				}
				r.setSubmittedDate(LocalDate.now());
				jr = JsonResponse.getInstance(requestRepo.save(r));
			} else {
				jr = JsonResponse.getInstance("error updating request id: " + r.getId() + " doesn't exist");
			}
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}

		return jr;
	}

	@PutMapping("/approve")

	// update request set status = 'review' where id = ?
	public JsonResponse approveRequest(@RequestBody Request r) {
		JsonResponse jr = null;
		try {
			if (requestRepo.existsById(r.getId())) {
				r.setStatus("approved");

				jr = JsonResponse.getInstance(requestRepo.save(r));
			} else {
				jr = JsonResponse.getInstance("error updating request id: " + r.getId() + " doesn't exist");
			}
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}

		return jr;
	}

	@PutMapping("/reject")
	// update request set status = 'reject' , reasonForRejection = ? where id =?
	public JsonResponse rejectRequest(@RequestBody Request r) {
		JsonResponse jr = null;
		// The full request will already give us the "reason for rejection"
		// String reasonForRejection = "test"; //need to remove this
		try {
			if (requestRepo.existsById(r.getId())) {
				r.setStatus("reject");
				// r.setReasonForRejection(reasonForRejection);
				jr = JsonResponse.getInstance(requestRepo.save(r));
			} else {
				jr = JsonResponse.getInstance("error updating request id: " + r.getId() + " doesn't exist");
			}
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}

		return jr;
	}

	@DeleteMapping("/{id}")
	public JsonResponse deleteRequest(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			if (requestRepo.existsById(id)) {
				requestRepo.deleteById(id);
				jr = JsonResponse.getInstance("Delete successful");
			} else {
				jr = JsonResponse.getInstance("error deleting request id: " + id + " doesn't exist");
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

}