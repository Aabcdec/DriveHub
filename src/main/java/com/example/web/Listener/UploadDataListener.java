package com.example.web.Listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.example.web.Bean.TClue;
import com.example.web.Bean.TUser;
import com.example.web.Mapper.TClueDao;
import jdk.nashorn.internal.parser.JSONParser;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


public class UploadDataListener implements ReadListener<TClue> {

    /**
     * 每隔100条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;

    //缓存List
    private List<TClue> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    @Resource
    private TClueDao tClueDao;
    private Integer createId;

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param tClueDao
     */
    public UploadDataListener(TClueDao tClueDao,int createId) {
        this.tClueDao = tClueDao;
        this.createId=createId;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param tClue    one row value. It is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(TClue tClue, AnalysisContext context) {
        //给读到的clue对象设置创建时间(导入时间)和创建人（导入人）
        tClue.setCreateTime(new Date());

        //这里应该拿到当前登录人id
        tClue.setCreateBy(this.createId);

        //每读取一行，就把该数据放入到一个缓存List中
        cachedDataList.add(tClue);

        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            //把缓存list中的数据写入到数据库
            saveData();

            //存储完成清空list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();

    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        System.out.println(cachedDataList);
        //这里能拿到数据
        tClueDao.saveClue(cachedDataList);
    }
}