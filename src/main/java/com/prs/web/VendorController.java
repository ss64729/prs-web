package com.prs.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.prs.business.Vendor;
import com.prs.db.VendorRepository;

@CrossOrigin
@RestController
@RequestMapping("/vendors")
public class VendorController {
	@Autowired
	VendorRepository vendorRepo;

	// Return all vendor with logging
	@GetMapping("/")
	public JsonResponse listVendor() {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(vendorRepo.findAll());

		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	@GetMapping("/{id}")
	public JsonResponse getVendor(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(vendorRepo.findById(id));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}

		return jr;
	}

	@PostMapping("/")
	public JsonResponse addVendor(@RequestBody Vendor v) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(vendorRepo.save(v));
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
	public JsonResponse updateVendor(@RequestBody Vendor v) {
		JsonResponse jr = null;
		try {
			if (vendorRepo.existsById(v.getId())) {
				jr = JsonResponse.getInstance(vendorRepo.save(v));
			} else {
				jr = JsonResponse.getInstance("error updating vendor id: " + v.getId() + " doesn't exist");
			}
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}

		return jr;
	}

	@DeleteMapping("/{id}")
	public JsonResponse deleteVendor(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			if (vendorRepo.existsById(id)) {
				vendorRepo.deleteById(id);
				jr = JsonResponse.getInstance("Delete successful");
			} else {
				jr = JsonResponse.getInstance("error deleting vendor id: " + id + " doesn't exist");
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