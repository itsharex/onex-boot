package com.nb6868.onex.coder.utils;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 自定义异常
 *
 * @author Charles (zhangchoxu@gmail.com)
 */
@Getter
@Setter
public class OnexException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;

    public OnexException(String msg) {
		super(msg);
		this.msg = msg;
	}

	public OnexException(String msg, Throwable e) {
		super(msg, e);
		this.msg = msg;
	}

	public OnexException(String msg, int code) {
		super(msg);
		this.msg = msg;
		this.code = code;
	}

	public OnexException(String msg, int code, Throwable e) {
		super(msg, e);
		this.msg = msg;
		this.code = code;
	}

}
