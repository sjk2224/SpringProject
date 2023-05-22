package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.AlbumFileDomain;
import com.co.kr.domain.AlbumListDomain;
import com.co.kr.domain.AlbumListFileDomain;
import com.co.kr.domain.AlbumURLDomain;

@Mapper
public interface UploadMapper {
	//Read (Album) 
	public List<AlbumListDomain> albumList();
	
	//Read (Album File) 
	public List<AlbumFileDomain> albumFileList();
	
	//Read (Album File) 
	public List<AlbumListFileDomain> albumFileViewList();
	
	//Read (Album File Page) 
	public List<AlbumListFileDomain> albumFileViewListPage(Map<String, Integer> map);	
			
	//Album insert
	public void URLupload(AlbumURLDomain albumURLDomain);
	
	//file insert
	public void fileUpload(AlbumFileDomain albumFileDomain);
	
	//Album update
	public void AlbumURLUpdate(AlbumURLDomain albumURLDomain);
	
	//file update
	public void AlbumFileUpdate(AlbumFileDomain albumFileDomain);
	
	//Album Delete
	public void AlbumURLRemove(HashMap<String, Object> map);
	
	//file Delete
	public void AlbumFileRemove(AlbumFileDomain albumFileDomain);
	
	public AlbumListDomain AlbumSelectOne(HashMap<String, Object> map);
	
	public List<AlbumFileDomain> AlbumSelectOneFile(HashMap<String, Object> map);
	
}
