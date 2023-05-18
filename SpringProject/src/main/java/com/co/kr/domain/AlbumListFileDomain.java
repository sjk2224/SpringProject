package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "builder")
public class AlbumListFileDomain {
	private String SongSeq;
	private String mbId;
	private String SongTitle;
	private String SongSinger;
	private String SongURL;
	private String SongCreateAt;
	private String SongUpdateAt;
	
	private String upOrginalFileName;
	private String upNewFileName;
	private String upFilePath;
	private Integer upFileSize;

}
