package com.example.funsdkdemo.devices;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.basic.G;
import com.example.common.DialogInputPasswd;
import com.example.funsdkdemo.ActivityDemo;
import com.example.funsdkdemo.R;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.config.JsonConfig;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.sensor.ISensorTips;
import com.lib.funsdk.support.sensor.SensorCommon;
import com.lib.sdk.bean.GetAllDevListBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.InquiryStatusBean;
import com.lib.sdk.bean.OPConsumerProCmdBean;
import com.lib.sdk.bean.OPConsumerProCmdBeanV2;
import com.lib.sdk.bean.StringUtils;

import java.util.List;

/**
 * @author hws
 * @name FunSDKDemo_Android_Old2018
 * @class name：com.example.funsdkdemo.devices
 * @class 433设备
 * @time 2019-03-26 17:49
 */
public class ActivityGuideDeviceSmart433 extends ActivityDemo
        implements IFunSDKResult,AdapterView.OnItemLongClickListener,AdapterView.OnItemClickListener{
    private ListView devList;
    private FunDevice funDevice;
    private OPConsumerProCmdBean cmdBean;
    private List<GetAllDevListBean> getAllDevListBeans;
    private DevListAdapter adapter;
    private int userId;
    private int operationStatus;
    private String operationDevName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_smart_433);
        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.backBtnInTopLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        devList = findViewById(R.id.lv_device_list);
        devList.setOnItemLongClickListener(this);
        devList.setOnItemClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        adapter = new DevListAdapter();
        devList.setAdapter(adapter);
        int devPos = intent.getIntExtra("FUN_DEVICE_ID", 0);
        funDevice = FunSupport.getInstance().findDeviceById(devPos);
        userId = FunSDK.GetId(userId,this);
        updateDevList();
    }
    public void onAddDevice(View view) {
        cmdBean = new OPConsumerProCmdBean();
        cmdBean.setCmd(JsonConfig.OPERATION_CMD_ADD);
        cmdBean.setArg1("60000");
        FunSDK.DevCmdGeneral(userId, funDevice.getDevSn(), 2046, JsonConfig.OPERATION_CMD_ADD, -1, 60000,
                HandleConfigData.getSendData(JsonConfig.OPERATION_CONSUMER_PRO_CMD, "0x01", cmdBean).getBytes(), -1, 0);
    }

    @Override
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        switch (message.what) {
            case EUIMSG.DEV_CMD_EN:
                if (JsonConfig.OPERATION_CMD_ADD.equals(msgContent.str)) {
                    if (msgContent.pData != null) {
                        JSONObject jobj = JSON.parseObject(G.ToString(msgContent.pData));
                        if (jobj != null && jobj.getIntValue("Ret") == 100) {
                            showToast(R.string.add_device_s);
                            updateDevList();
                            break;
                        }
                    }
                    showToast(R.string.add_device_f);
                }else if (JsonConfig.OPERATION_CMD_GET.equals(msgContent.str)) {
                    if (msgContent.pData != null) {
                        HandleConfigData handleConfigData = new HandleConfigData();
                        if(handleConfigData.getDataObj(G.ToString(msgContent.pData), GetAllDevListBean.class)) {
                            getAllDevListBeans = (List<GetAllDevListBean>) handleConfigData.getObj();
                            if (getAllDevListBeans != null) {
                                for (int i = 0; i < getAllDevListBeans.size(); ++i) {
                                    getInquiryStatus(getAllDevListBeans.get(i).DevID,i);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }else if (JsonConfig.OPERATION_CMD_INQUIRY_STATUS.equals(msgContent.str)) {
                    if (message.arg1 >= 0) {
                        GetAllDevListBean bean = getAllDevListBeans.get(msgContent.seq);
                        if (bean != null && msgContent.pData != null) {
                            HandleConfigData data = new HandleConfigData();
                            if(data.getDataObj(G.ToString(msgContent.pData),InquiryStatusBean.class)) {
                                InquiryStatusBean inquiryStatusBean = (InquiryStatusBean) data.getObj();
                                if (inquiryStatusBean != null) {
                                    bean.setFunctionStatus(inquiryStatusBean.getDevStatus());
                                }
                            }
                            ISensorTips sensorTips = new SensorCommon();
                            String tips = sensorTips.getTips(G.ToString(msgContent.pData));
                            bean.tips = tips;
                            adapter.notifyDataSetChanged();
                        }
                    }
                }else if (StringUtils.contrast(msgContent.str, JsonConfig.OPERATION_CMD_DEL)) {
                    if (message.arg1 >= 0) {
                        getAllDevListBeans.remove(msgContent.seq);
                        showToast(getString(R.string.delete_s));
                        adapter.notifyDataSetChanged();
                    }else {
                        showToast(getString(R.string.delete_f));
                    }
                }else if (StringUtils.contrast(msgContent.str,JsonConfig.OPERATION_CMD_STATUS)) {
                    if (message.arg1 >= 0) {
                        GetAllDevListBean bean = getAllDevListBeans.get(msgContent.seq);
                        if (bean != null) {
                            bean.setFunctionStatus(operationStatus);
                        }
                        adapter.notifyDataSetChanged();
                        showToast(getString(R.string.change_status_s));
                    }else {
                        showToast(getString(R.string.change_status_f));
                    }
                }else if (StringUtils.contrast(msgContent.str,JsonConfig.OPERATION_CMD_RENAME)) {
                    if (message.arg1 >= 0) {
                        GetAllDevListBean bean = getAllDevListBeans.get(msgContent.seq);
                        if (bean != null) {
                            bean.DevName = operationDevName;
                        }
                        adapter.notifyDataSetChanged();
                        showToast(getString(R.string.change_status_s));
                    }else {
                        showToast(getString(R.string.change_status_f));
                    }
                }
                break;
        }
        return 0;
    }

    private void updateDevList() {
        FunSDK.DevCmdGeneral(userId, funDevice.getDevSn(), OPConsumerProCmdBean.JSON_ID, JsonConfig.OPERATION_CMD_GET, -1, 60000,
                OPConsumerProCmdBean.getCmdJson(JsonConfig.OPERATION_CMD_GET,"","").getBytes(), -1, 0);
    }

    private void getInquiryStatus(String devId,int position) {
        FunSDK.DevCmdGeneral(userId, funDevice.getDevSn(), OPConsumerProCmdBean.JSON_ID, JsonConfig.OPERATION_CMD_INQUIRY_STATUS, -1, 60000,
                OPConsumerProCmdBean.getCmdJson(JsonConfig.OPERATION_CMD_INQUIRY_STATUS, devId,"").getBytes(), -1, position);
    }

    private void deleteDev(String devId,int position) {
        FunSDK.DevCmdGeneral(userId, funDevice.getDevSn(), OPConsumerProCmdBean.JSON_ID, JsonConfig.OPERATION_CMD_DEL, -1, 60000,
                OPConsumerProCmdBean.getCmdJson(JsonConfig.OPERATION_CMD_DEL, devId,"").getBytes(), -1, position);
    }

    private void changeStatus(String devId,int position,int status) {
        this.operationStatus = status;
        FunSDK.DevCmdGeneral(userId, funDevice.getDevSn(), OPConsumerProCmdBeanV2.JSON_ID, JsonConfig.OPERATION_CMD_STATUS, -1, 60000,
                OPConsumerProCmdBeanV2.getCmdJson(JsonConfig.OPERATION_CMD_STATUS, devId,"001",status).getBytes(), -1, position);
    }

    private void changeDevName(String devId,int position,String devName) {
        this.operationDevName = devName;
        FunSDK.DevCmdGeneral(userId, funDevice.getDevSn(), OPConsumerProCmdBean.JSON_ID, JsonConfig.OPERATION_CMD_RENAME, -1, 60000,
                OPConsumerProCmdBean.getCmdJson(JsonConfig.OPERATION_CMD_RENAME, devId,devName).getBytes(), -1, position);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (position < getAllDevListBeans.size()) {
            GetAllDevListBean data = getAllDevListBeans.get(position);
            if (data != null) {
                deleteDev(data.DevID,position);
            }
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,final int position, long l) {

    }

    class DevListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return getAllDevListBeans != null ? getAllDevListBeans.size() : 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (view == null) {
                view =  LayoutInflater.from(ActivityGuideDeviceSmart433.this).inflate(R.layout.item_smart_device_list,viewGroup,false);
                viewHolder = new ViewHolder();
                viewHolder.textView = view.findViewById(R.id.textView);
                viewHolder.btnChangeStatus = view.findViewById(R.id.btnChangeStatus);
                viewHolder.btnChangeDevName = view.findViewById(R.id.btnChangeDevName);
                viewHolder.btnDelete = view.findViewById(R.id.btnDelete);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }
            final GetAllDevListBean data = getAllDevListBeans.get(position);
            if (data != null) {
                String text = data.DevName + ":";
                if (data.getFunctionStatus() == SDKCONST.Switch.Close) {
                    text += FunSDK.TS("Close");
                }else if (data.getFunctionStatus() == SDKCONST.Switch.Open) {
                    text += FunSDK.TS("Open");
                }else if (data.getFunctionStatus() == SDKCONST.Switch.Pause) {
                    text += FunSDK.TS("Pause");
                }
                viewHolder.textView.setText(text);
                viewHolder.btnChangeStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeStatus(data.DevID,position,data.getFunctionStatus() == SDKCONST.Switch.Open
                                ? SDKCONST.Switch.Close : SDKCONST.Switch.Open);
                    }
                });
                viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteDev(data.DevID,position);
                    }
                });
                viewHolder.btnChangeDevName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogInputPasswd inputDialog = new DialogInputPasswd(ActivityGuideDeviceSmart433.this,
                                getResources().getString(R.string.input_device_name), "", R.string.common_confirm,
                                R.string.common_cancel) {

                            @Override
                            public boolean confirm(String editText) {
                                if (position < getAllDevListBeans.size()) {
                                    GetAllDevListBean data = getAllDevListBeans.get(position);
                                    if (data != null) {
                                        changeDevName(data.DevID,position,editText);
                                    }
                                }
                                return super.confirm(editText);
                            }

                            @Override
                            public void cancel() {
                                super.cancel();
                            }
                        };
                        inputDialog.show();
                    }
                });
            }
            return view;
        }

        class ViewHolder {
            TextView textView;
            Button btnChangeStatus;
            Button btnDelete;
            Button btnChangeDevName;
        }
    }
}
