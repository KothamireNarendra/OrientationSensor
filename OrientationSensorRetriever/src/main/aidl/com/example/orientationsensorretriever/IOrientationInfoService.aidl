package com.example.orientationsensorretriever;

import com.example.orientationsensorretriever.IOrientationInfoCallback;

interface IOrientationInfoService{

    void registerCallBack(in IOrientationInfoCallback orientationInfoCallBack);

    void unRegisterCallBack(in IOrientationInfoCallback orientationInfoCallBack);
}