package com.ucpaas.smsp.model;

import java.util.Date;
import java.util.Objects;

import com.ucpaas.smsp.util.encrypt.EncryptUtils;

public class SmsOauthPic {
    private Integer id;

    private Integer agentId;

    private String clientId;

    private String idNbr;

    private String imgUrl;

    private Integer idType;

    private Integer idOrder;

    private String idDesc;

    private Date createDate;

    private Date updateDate;

    private Integer status;

    private String idNbr2;

    private String idNbr3;

    private String remotePath;
    
    private Integer oauthStatus; //认证状态，2：待认证 ，3：证件已认证(正常)，4：认证不通过
    
    private String realname;
    
    private Integer clientType; // 用户类型，1：个人用户，2：企业用户

    private Integer oauthType; // 认证类型： 1、代理商资质认证 2、客户资质认证
    
    private String reason;
    
	public Integer getOauthType() {
		return oauthType;
	}

	public void setOauthType(Integer oauthType) {
		this.oauthType = oauthType;
	}

	public Integer getClientType() {
		return clientType;
	}

	public void setClientType(Integer clientType) {
		this.clientType = clientType;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId == null ? null : clientId.trim();
    }

    public String getIdNbr() {
        return idNbr;
    }

    public void setIdNbr(String idNbr) {
        this.idNbr = idNbr == null ? null : idNbr.trim();
    }

    public String getImgUrl() {
//    	return EncryptUtils.encodeDes3(imgUrl);
    	return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
    	this.imgUrl = imgUrl;
//        this.imgUrl = EncryptUtils.encodeDes3(imgUrl);
    }

    public Integer getIdType() {
        return idType;
    }

    public void setIdType(Integer idType) {
        this.idType = idType;
    }

    public Integer getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(Integer idOrder) {
        this.idOrder = idOrder;
    }

    public String getIdDesc() {
        return idDesc;
    }

    public void setIdDesc(String idDesc) {
        this.idDesc = idDesc == null ? null : idDesc.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getIdNbr2() {
        return idNbr2;
    }

    public void setIdNbr2(String idNbr2) {
        this.idNbr2 = idNbr2 == null ? null : idNbr2.trim();
    }

    public String getIdNbr3() {
        return idNbr3;
    }

    public void setIdNbr3(String idNbr3) {
        this.idNbr3 = idNbr3 == null ? null : idNbr3.trim();
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath == null ? null : remotePath.trim();
    }
    
	public Integer getOauthStatus() {
		return oauthStatus;
	}

	public void setOauthStatus(Integer oauthStatus) {
		this.oauthStatus = oauthStatus;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "SmsOauthPic [id=" + id + ", agentId=" + agentId + ", clientId=" + clientId + ", idNbr=" + idNbr
				+ ", imgUrl=" + imgUrl + ", idType=" + idType + ", idOrder=" + idOrder + ", idDesc=" + idDesc
				+ ", createDate=" + createDate + ", updateDate=" + updateDate + ", status=" + status + ", idNbr2="
				+ idNbr2 + ", idNbr3=" + idNbr3 + ", remotePath=" + remotePath + ", oauthStatus=" + oauthStatus
				+ ", realname=" + realname + ", clientType=" + clientType + ", oauthType=" + oauthType + ", reason="
				+ reason + "]";
	}

//	public CommonsMultipartFile getFile() {
//		return photoFile;
//	}
//
//	public void setFile(CommonsMultipartFile photoFile) {
//		this.photoFile = photoFile;
//	}
}