package com.co.kr.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.domain.AlbumFileDomain;
import com.co.kr.domain.AlbumListDomain;
import com.co.kr.domain.AlbumListFileDomain;
import com.co.kr.vo.FileListVO;

public interface UploadService {
	//Read (Album)
	public List<AlbumListDomain> albumList();
	
	//Read (File)
	public List<AlbumFileDomain> albumFileList();
	
	//Read (Album File View)
	public List<AlbumListFileDomain> albumFileViewList();
	
	//Insert and Update
	public int fileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq);
	
	//Remove Album
	public void AlbumURLRemove(HashMap<String, Object> map);
	
	//Remove File
	public void AlbumFileRemove(AlbumFileDomain albumFileDomain);
	
	public AlbumListDomain AlbumSelectOne(HashMap<String, Object> map);
	
	public List<AlbumFileDomain> AlbumSelectOneFile(HashMap<String, Object> map);
	
}
