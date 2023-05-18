package com.co.kr.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.code.Code;
import com.co.kr.domain.AlbumFileDomain;
import com.co.kr.domain.AlbumListDomain;
import com.co.kr.domain.AlbumListFileDomain;
import com.co.kr.domain.AlbumURLDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.mapper.UploadMapper;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.FileListVO;

@Service
public class UploadServiceImple implements UploadService{

	@Autowired
	private UploadMapper uploadMapper;
	
	@Override
	public List<AlbumListDomain> albumList() {
		// TODO Auto-generated method stub
		return uploadMapper.albumList();
	}

	@Override
	public List<AlbumFileDomain> albumFileList() {
		// TODO Auto-generated method stub
		return uploadMapper.albumFileList();
	}
	@Override
	public int fileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) {
		// TODO Auto-generated method stub
		HttpSession session = httpReq.getSession();
		
		AlbumURLDomain albumURLDomain = AlbumURLDomain.builder()
				.mbId(session.getAttribute("id").toString())
				.SongTitle(fileListVO.getSongtitle())
				.SongSinger(fileListVO.getSongSinger())
				.SongURL(fileListVO.getSongURL())
				.build();
		
		if(fileListVO.getSongisEdit() != null) {
			albumURLDomain.setSongSeq(Integer.parseInt(fileListVO.getSongseq()));
			System.out.println("수정 업데이트");
			uploadMapper.AlbumURLUpdate(albumURLDomain);
		}else {
			//db insert
			uploadMapper.URLupload(albumURLDomain);
			System.out.println("db insert");
			System.out.println(albumURLDomain);
			
		}
		
		//file 데이터 db 저장시 쓰일 값
		int SongSeq = albumURLDomain.getSongSeq();
		String mbId = albumURLDomain.getMbId();
		
		//파일 객체
		List<MultipartFile> multipartFiles = request.getFiles("files");
		System.out.println(fileListVO.getSongisEdit());
		
		if(fileListVO.getSongisEdit() != null) {
			List<AlbumFileDomain> fileList = null;
			
			for(MultipartFile multipartFile : multipartFiles) {
				if(!multipartFile.isEmpty()) {
					System.out.println("session.getAttribute(\"files\") ==>"+session.getAttribute("files"));
					if(session.getAttribute("files") !=  null) {
						
						fileList = (List<AlbumFileDomain>) session.getAttribute("files");
						System.out.println("fileList ==>"+fileList);
						for(AlbumFileDomain list : fileList) {
							list.getUpFilePath();
							Path filePath = Paths.get(list.getUpFilePath());
							
							try {
								Files.deleteIfExists(filePath);
								AlbumFileRemove(list);
							}catch(DirectoryNotEmptyException e) {
								throw RequestException.fire(Code.E404,"디렉토리가 존재하지 않습니다.",HttpStatus.NOT_FOUND);
							}catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		
		Path rootPath = Paths.get(new File("C://").toString(),"board_upload",File.separator).toAbsolutePath().normalize();
		File pathCheck = new File(rootPath.toString());
		
		if(!pathCheck.exists()) pathCheck.mkdirs();
		
		for (MultipartFile multipartFile: multipartFiles) {
			if(!multipartFile.isEmpty()) {
				String originalFileExtension;
				String contentType = multipartFile.getContentType();
				String origFilename = multipartFile.getOriginalFilename();
				
				if(ObjectUtils.isEmpty(contentType)) {
					break;
				}else {
					if(contentType.contains("image/jpeg")) {
						originalFileExtension = ".jpg";
					}else if (contentType.contains("image/png")) {
						originalFileExtension = ".png";
					}else {
						break;
					}
				}
				
				//파일명을 업로드 날짜로 변환하여 저장
				String uuid = UUID.randomUUID().toString();
				String current = CommonUtils.currentTime();
				String newFilName = uuid + current + originalFileExtension;
				
				//최종 경로까지 지정
				Path targetPath = rootPath.resolve(newFilName);
				
				File file = new File(targetPath.toString());
				
				try {
					multipartFile.transferTo(file);
					
					file.setWritable(true);
					file.setReadable(true);
					
					AlbumFileDomain albumFileDomain = AlbumFileDomain.builder()
							.SongSeq(SongSeq)
							.mbId(mbId)
							.upOrginalFileName(origFilename)
							.upNewFileName("resources/board_upload/" + newFilName)
							.upFilePath(targetPath.toString())
							.upFileSize((int)multipartFile.getSize())
							.build();
					
					uploadMapper.fileUpload(albumFileDomain);
					System.out.println("upload done");
				}catch (IOException e) {
					// TODO: handle exception
					throw RequestException.fire(Code.E404,"잘모된 업로드 파일", HttpStatus.NOT_FOUND);
				}
			}
		}
		
		return SongSeq;
	}

	@Override
	public void AlbumURLRemove(HashMap<String, Object> map) {
		// TODO Auto-generated method stub
		uploadMapper.AlbumURLRemove(map);
	}

	@Override
	public void AlbumFileRemove(AlbumFileDomain albumFileDomain) {
		// TODO Auto-generated method stub
		uploadMapper.AlbumFileRemove(albumFileDomain);
	}

	@Override
	public AlbumListDomain AlbumSelectOne(HashMap<String, Object> map) {
		// TODO Auto-generated method stub
		return uploadMapper.AlbumSelectOne(map);
	}

	@Override
	public List<AlbumFileDomain> AlbumSelectOneFile(HashMap<String, Object> map) {
		// TODO Auto-generated method stub
		return uploadMapper.AlbumSelectOneFile(map);
	}

	@Override
	public List<AlbumListFileDomain> albumFileViewList() {
		// TODO Auto-generated method stub
		return uploadMapper.albumFileViewList();
	}

	

}
