package com.co.kr.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

@Component
public class Pagination {
	
	public static Map<String,Object> pagination(int totalcount, HttpServletRequest request) {
		Map<String,Object> map = new HashMap<String,Object>();
		// 페이지넘버 초기화
		//String pnum = request.getParameter("page");
		
		HttpSession session = request.getSession();
		String pnum = (String) session.getAttribute("page");
		
		System.out.println("pnum : "+pnum);
		if (pnum == null) { pnum = "1"; }
		
		// 스트링을 인트로 파싱
		int rowNUM = Integer.parseInt(pnum);
		if(rowNUM < 0) {rowNUM = 1;}
		
		// 페이지네이션 범위정함 나머지 없거나 있으면 +1
		int pageNum;
		if (totalcount % 12 == 0) { 
			pageNum = totalcount / 12; 
		}else { 
			pageNum = (totalcount / 12) + 1; 
		}
		if(rowNUM > pageNum) { rowNUM = pageNum; }
		
		// 쿼리 범위 지정
		int offset = (rowNUM - 1) * 12;
	
		map.put("rowNUM", rowNUM);
		map.put("pageNum", pageNum);
		map.put("offset", offset);

		return map;
	}

}