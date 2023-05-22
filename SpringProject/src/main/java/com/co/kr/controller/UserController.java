package com.co.kr.controller;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.code.Code;
import com.co.kr.domain.AlbumFileDomain;
import com.co.kr.domain.AlbumListDomain;
import com.co.kr.domain.AlbumListFileDomain;
import com.co.kr.domain.LoginDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.service.UploadService;
import com.co.kr.service.UserService;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.FileListVO;
import com.co.kr.vo.LoginVO;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j 
@RequestMapping(value = "/")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UploadService uploadService;
	
	// 진입점
	@GetMapping("/")
	public String index(){
		return "index.html";
	}

	@RequestMapping(value = "signin")
	public String UserSignIn(){
		return "signin/signin.html";
	}

	@PostMapping("MemberCreate")
	public ModelAndView MemberCreate(LoginVO loginVO, HttpServletRequest request, HttpServletResponse response) throws IOException{
		ModelAndView mav = new ModelAndView();
		
		HttpSession session = request.getSession();
		
		HashMap<String, String> map = new HashMap<String,String>();
		map.put("mbId", loginVO.getId());
		map.put("mbPw", loginVO.getPw());
		
		int dupleCheck = userService.mbDuplicationCheck(map);
		System.out.println(dupleCheck);
		
		if(dupleCheck > 0) {
			String alertText = "중복이거나 유효하지 않은 접근입니다.";
			String redirectPath = "/main.do/signin";
			System.out.println(loginVO.getAdmin());
			if(loginVO.getAdmin() != null) {
				redirectPath = "/main.do/AlbumList";
			} 
			CommonUtils.redirect(alertText, redirectPath, response);
		}else {
			
			String IP = CommonUtils.getClientIP(request);
			
			int totalCount = userService.mbGetAll();
			
			LoginDomain loginDomain = LoginDomain.builder()
					.mbId(loginVO.getId())
					.mbPw(loginVO.getPw())
					.mbLevel((totalCount == 0) ? "3" : "2")
					.mbIp(IP)
					.mbUse("Y")
					.build();
			
			userService.mbCreate(loginDomain);
			
			if(loginVO.getAdmin() == null) {
				session.setAttribute("ip", IP);
				session.setAttribute("id", loginDomain.getMbId());
				session.setAttribute("mbLevel",(totalCount==0) ? "3" : "2");
				mav.setViewName("redirect:/AlbumList");
			}else {
				mav.setViewName("redirect:/AlbumList");
			}
		}
		
		return mav;
	}
	
	@RequestMapping(value = "Modify")
	public ModelAndView UserModify(HttpServletRequest request){
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
	
		Map<String,String> map= new HashMap<String, String>();
		
		map.put("mbId", session.getAttribute("id").toString());
	
		LoginDomain loginDomain = userService.mbSelectList(map);
		mav.addObject("item",loginDomain);

		mav.setViewName("member/memberDetail.html");
		return mav;
	}
	
	@PostMapping("MemberUpdate")
	public ModelAndView MemberUpdate(LoginVO loginVO, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		
		HttpSession session = request.getSession();
		
		System.out.println(loginVO);
		
		LoginDomain loginDomain = null;
		String IP = CommonUtils.getClientIP(request);
		loginDomain = LoginDomain.builder()
				.mbSeq(Integer.parseInt(loginVO.getSeq()))
				.mbId(loginVO.getId())
				.mbPw(loginVO.getPw())
				.mbLevel(loginVO.getLevel())
				.mbIp(IP)
				.mbUse("Y")
				.build();
		System.out.println(loginDomain);
		
		userService.mbUpdate(loginDomain);
		
		mav.setViewName("redirect:Modify");
		return mav;
	}
	
	@RequestMapping(value = "MemberRemove")
	public ModelAndView UserRemove(HttpServletRequest request) throws IOException{
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
	
		Map<String,String> map= new HashMap<String, String>();
		
		map.put("mbId", session.getAttribute("id").toString());
		
		List<AlbumFileDomain> fileList = uploadService.MemberAlbumFileList(map);
		System.out.println("pass");
		
		for(AlbumFileDomain list : fileList) {
			list.getUpFilePath();
			Path filePath = Paths.get(list.getUpFilePath());
			
			try {
				Files.deleteIfExists(filePath);
			}catch(DirectoryNotEmptyException e) {
				throw RequestException.fire(Code.E404,"디렉토리가 존재하지 않습니다.",HttpStatus.NOT_FOUND);
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		uploadService.MemberAlbumFileRemove(map);
		uploadService.MemberAlbumURLRemove(map);
		
		userService.mbRemove(map);
		
		mav.setViewName("redirect:/");
		return mav;
	}
	
	@RequestMapping(value = "album")
	public ModelAndView login(LoginVO loginDTO, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		//session 처리 
		HttpSession session = request.getSession();
		ModelAndView mav = new ModelAndView();
		// 중복체크
		Map<String, String> map = new HashMap();
		map.put("mbId", loginDTO.getId());
		map.put("mbPw", loginDTO.getPw());
		
		// 중복체크
		int dupleCheck = userService.mbDuplicationCheck(map);
		LoginDomain loginDomain = userService.mbGetId(map);
		
		if(dupleCheck == 0) {  
			String alertText = "없는 아이디이거나 패스워드가 잘못되었습니다. 가입해주세요";
			String redirectPath = "/main.do/";
			CommonUtils.redirect(alertText, redirectPath, response);
			return mav;
		}


		//현재아이피 추출
		String IP = CommonUtils.getClientIP(request);
		
		//session 저장
		session.setAttribute("ip",IP);
		session.setAttribute("id", loginDomain.getMbId());
		session.setAttribute("mbLevel", loginDomain.getMbLevel());

		mav.setViewName("redirect:/AlbumList"); 
		
		return mav;
	};
	
	@RequestMapping("Logout")
	public ModelAndView Logout(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		session.invalidate();
		mav.setViewName("index.html");
		return mav;
	}
}