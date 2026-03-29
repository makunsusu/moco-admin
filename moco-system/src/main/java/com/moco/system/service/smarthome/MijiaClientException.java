package com.moco.system.service.smarthome;

public class MijiaClientException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public MijiaClientException(String message)
    {
        super(message);
    }

    public MijiaClientException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
