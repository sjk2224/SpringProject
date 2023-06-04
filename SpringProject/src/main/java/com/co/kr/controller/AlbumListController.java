package com.co.kr.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.code.Code;
import com.co.kr.domain.AlbumFileDomain;
import com.co.kr.domain.AlbumListDomain;
import com.co.kr.domain.AlbumListFileDomain;
import com.co.kr.domain.AlbumURLDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.service.UploadService;
import com.co.kr.util.Pagination;
import com.co.kr.vo.FileListVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class AlbumListController {
	
	@Autowired
	private UploadService uploadService;
	
	@RequestMapping(value = "AlbumList")
	public ModelAndView AlbumList(HttpServletRequest request,
			@RequestParam(required = false) String page,
			@RequestParam(required = false) String searchWord) {
		ModelAndView mav = new ModelAndView();
		
		HttpSession session = request.getSession();
		System.out.println(session.getAttribute("id").toString());
		
		if(page == null)
			page = "1";
		
		if(searchWord == null) {
			searchWord = "";
		}
	
		System.out.println("search word : " + searchWord);
		
		session.setAttribute("page", page);
		
		mav = AlbumListCall(request,searchWord);
		mav.addObject("mbId",session.getAttribute("id"));
		mav.addObject("searchWord",searchWord);
		
		mav.setViewName("board/album.html");
		return mav;
	}
	
	public ModelAndView AlbumListCall(HttpServletRequest request,String SearchWord) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		HashMap<String, String> searchWordMap = new HashMap<>();
		
		searchWordMap.put("searchWord", SearchWord);
		searchWordMap.put("mbId", session.getAttribute("id").toString());
		System.out.println("pass : " + searchWordMap);
		
		int totalcount = uploadService.albumFileViewList(searchWordMap).size();
		int URLnum = 12;
		System.out.println("item.size ==> " + totalcount);
		
		boolean itemsNotEmpty;
		
		if(totalcount > 0) {
			itemsNotEmpty = true;
			
			Map<String, Object> pagination = Pagination.pagination(totalcount, request);
			
			Map map = new HashMap<String, Integer>();
				map.put("offset", pagination.get("offset"));
				map.put("URLnum",URLnum);
				map.put("mbId", session.getAttribute("id").toString());
				map.put("searchWord", SearchWord);
				
			List<AlbumListFileDomain> items = uploadService.albumFileViewListPage(map);
			System.out.println("items ==> " + items);
			
			mav.addObject("itemsNotEmpty", itemsNotEmpty);
			mav.addObject("items",items);
			mav.addObject("rowNUM", pagination.get("rowNUM"));
			mav.addObject("pageNum", pagination.get("pageNum"));
			mav.addObject("startpage", pagination.get("startpage"));
			
		}else {
			itemsNotEmpty = false;
		}
		return mav;
		
	}
	
	@RequestMapping(value = "Create")
	public String albumCreate(){
		return "board/albumCreate.html";
	}
	
	@PostMapping(value = "upload")
	public ModelAndView albumUpload(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException, ParseException {
		
		ModelAndView mav = new ModelAndView();
		int SongSeq = uploadService.fileProcess(fileListVO,request,httpReq);
		fileListVO.setSongSinger("");
		fileListVO.setSongURL("");
		fileListVO.setSongtitle("");
		
		mav = SongSelectOne(fileListVO,String.valueOf(SongSeq),request);
		mav.setViewName("redirect:/AlbumList");
		return mav;
	}
	
	public ModelAndView SongSelectOne(@ModelAttribute("fileListVO") FileListVO fileListVO, String SongSeq, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		
		map.put("SongSeq", Integer.parseInt(SongSeq));
		System.out.println(map);
		AlbumListDomain albumListDomain = uploadService.AlbumSelectOne(map);
		List<AlbumFileDomain> fileList = uploadService.AlbumSelectOneFile(map);
		
		for(AlbumFileDomain list: fileList) {
			String path = list.getUpFilePath().replace("\\\\", "/");
			list.setUpFilePath(path);
		}
		
		System.out.println("AlbumListDomain : " + albumListDomain);
		System.out.println("File : " + fileList);
		mav.addObject("detail",albumListDomain);
		mav.addObject("files",fileList);
		session.setAttribute("files", fileList);
		
		return mav;
	}
	
	@GetMapping(value = "Edit")
	public ModelAndView albumEdit(@ModelAttribute("fileListVO") FileListVO fileListVO , @RequestParam("SongSeq")String SongSeq ,HttpServletRequest request) throws IOException{
		ModelAndView mav = new ModelAndView();
		
		HashMap<String,Object> map = new HashMap<String,Object>();
		HttpSession session = request.getSession();
		
		map.put("SongSeq", Integer.parseInt(SongSeq));
		AlbumListDomain albumListDomain = uploadService.AlbumSelectOne(map);
		List<AlbumFileDomain> fileList = uploadService.AlbumSelectOneFile(map);

		for(AlbumFileDomain list: fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\","/");
			list.setUpFilePath(path);
		}	
		
		fileListVO.setSongseq(albumListDomain.getSongSeq());
		fileListVO.setSongSinger(albumListDomain.getSongSinger());
		fileListVO.setSongURL(albumListDomain.getSongURL());
		fileListVO.setSongtitle(albumListDomain.getSongTitle());
		fileListVO.setSongisEdit("edit");
		
		mav.addObject("detail", albumListDomain);
		mav.addObject("files",fileList);
		mav.addObject("fileLen",fileList.size());
		session.setAttribute("files", fileList);
		
		System.out.println("Edit album Source : " + albumListDomain);
		System.out.println("Edit file Source : " + fileList);

		mav.setViewName("board/albumDetail.html");
		return mav;
	}
	
	@PostMapping("EditSave")
	public ModelAndView AlbumEditSave(@ModelAttribute("fileListVO") FileListVO fileListVO, MultipartHttpServletRequest request ,HttpServletRequest httpReq) throws IOException {
		ModelAndView mav = new ModelAndView();
		
		uploadService.fileProcess(fileListVO, request, httpReq);
		mav = SongSelectOne(fileListVO, fileListVO.getSongseq(), request);
		fileListVO.setSongSinger("");
		fileListVO.setSongURL("");
		fileListVO.setSongtitle("");
		
		System.out.println("Edit album Source : " + mav);
		
		mav.setViewName("redirect:/AlbumList");
		
		return mav;
	}
	
	@GetMapping("Remove")
	public ModelAndView AlbumRemove(@RequestParam("SongSeq") String Songseq) throws IOException {
		ModelAndView mav = new ModelAndView();
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("SongSeq", Integer.parseInt(Songseq));
		
		List<AlbumFileDomain> fileList = uploadService.AlbumSelectOneFile(map);
		
		uploadService.AlbumURLRemove(map);
		
		for(AlbumFileDomain list : fileList) {
			list.getUpFilePath();
			Path filePath = Paths.get(list.getUpFilePath());
			
			try {
				Files.deleteIfExists(filePath);
				uploadService.AlbumFileRemove(list);
			}catch (DirectoryNotEmptyException e) {
				throw RequestException.fire(Code.E404, "디렉토리가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
			}catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		mav.setViewName("redirect:/AlbumList");
		
		return mav;
	}
	
	@PostMapping("Search")
	public ModelAndView Search(@RequestParam String searchWord) throws IOException{
		ModelAndView mav = new ModelAndView();
		System.out.println("search word 1 : " +searchWord);
		if(searchWord == null) {
			searchWord = "";
		}
		String searchString= URLEncoder.encode(searchWord,"UTF-8");
		String redicrectPath = "redirect:/AlbumList?page=1&searchWord=" + searchString;
		System.out.println("Path : " + redicrectPath);
		mav.setViewName(redicrectPath);
		return mav;
	}
}
