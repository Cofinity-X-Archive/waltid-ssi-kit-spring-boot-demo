package com.cofinityx.waltid.waltidssikitdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class DidDocumentRequest implements Serializable {

    private String tenant;

}
