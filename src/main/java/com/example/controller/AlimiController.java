package com.example.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.DTO.AlimiDto;
import com.example.domain.Alimi;
import com.example.security.SecurityUtil;
import com.example.service.AlimiService;

@RestController
@RequestMapping("/api")
public class AlimiController {
	
	//알라딘 api 정보(URL, KEY)
    private static final String API_URL = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx";
    private static final String API_KEY = "ttbskl54731543001";
    
	@Autowired AlimiService alimiService;
	
	//알리미 등록
	@PostMapping("/alimi")
	public ResponseEntity alimiAdd(@RequestBody AlimiDto params) {
		String email = SecurityUtil.getCurrentEmail();
		
		Alimi alimi = new Alimi();
		alimi.setBookName(params.getBookName());
		alimi.setIsbn(params.getIsbn());
		alimi.setRegDate(LocalDateTime.now());
		alimi.setSendStatus("N");
		alimi.setWriter(email);
		
		Alimi result = alimiService.save(alimi);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	//알라딘 조회
    @GetMapping("/aladinbooks")
    public ResponseEntity<String> searchBooks(@RequestParam("query") String query,
                                              @RequestParam(name = "queryType", defaultValue = "Title") String queryType,
                                              @RequestParam(defaultValue = "10") int maxResults,
                                              @RequestParam(defaultValue = "1") int start,
                                              @RequestParam(defaultValue = "Book") String searchTarget,
                                              @RequestParam(defaultValue = "js") String output,
                                              @RequestParam(defaultValue = "20131101") String version) {
        RestTemplate restTemplate = new RestTemplate();
        
        String apiUrl = API_URL + "?ttbkey=" + API_KEY + "&Query=" + query +
                "&QueryType=" + queryType + "&MaxResults=" + maxResults + "&start=" + start +
                "&SearchTarget=" + searchTarget + "&output=" + output + "&Version=" + version;
        
        String result = restTemplate.getForObject(apiUrl, String.class);
        
        return ResponseEntity.ok(result);
    }
    
    //내 알리미 목록 조회
    @GetMapping("/allalimi")
    public ResponseEntity getAllAlimi(@RequestParam("page") int page, @RequestParam("size") int size) {
    	String email = SecurityUtil.getCurrentEmail();
    	
    	Pageable pageable = PageRequest.of(page, size);
    	
    	Page<Alimi> alimis = alimiService.findByWriter(email, pageable); 
    	
    	return new ResponseEntity<>(alimis, HttpStatus.OK);
    }
    
    //내 알리미 삭제
    @DeleteMapping("/alimi")
    public ResponseEntity deleteMyAlimi(@RequestBody List<Integer> alimiIds) {
    	System.out.println("알리미아이디"+alimiIds);
    	for(var i= 0; i<alimiIds.size(); i++) {
    		Integer id = alimiIds.get(i);
    		alimiService.deleteById(id);
    	}
    	
    	return new ResponseEntity<>(HttpStatus.OK);
    }
}

