package com.meiqi.dsmanager.action.impl;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.meiqi.dsmanager.action.IMyBatisManualTransactionalAction;
import com.meiqi.mushroom.dao.ITMushroomServiceDao;
import com.meiqi.mushroom.dao.ITMushroomServiceTableDao;
import com.meiqi.mushroom.dao.ITMushroomTableDao;
import com.meiqi.mushroom.dao.ITMushroomTableFieldDao;
import com.meiqi.mushroom.entity.TMushroomService;
import com.meiqi.mushroom.entity.TMushroomServiceTable;
import com.meiqi.mushroom.entity.TMushroomTable;
import com.meiqi.mushroom.entity.TMushroomTableField;
import com.meiqi.util.BaseDao;
/**
 * mybatis手动事务
 * @author Administrator
 *
 */
@Service
public class MyBatisManualTransactionalActionImpl extends BaseDao implements IMyBatisManualTransactionalAction {

	@Autowired
	private ITMushroomServiceDao tMushroomServiceDao;
	@Autowired
	private ITMushroomTableDao tMushroomTableDao;
	@Autowired
	private ITMushroomServiceTableDao tMushroomServiceTableDao;
	@Autowired
	private ITMushroomTableFieldDao tMushroomTableFieldDao;

	
	@Override
	public void saveMushroomConfig(TMushroomService tMushroomService,
			TMushroomTable tMushroomTable,
			TMushroomTableField tMushroomTableField) throws Exception {
		//使用mybatis手动事务管理
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		SqlSession sqlSession = getSqlSessionTemplate().getSqlSessionFactory().openSession(false);
		sqlSession.getConnection().setAutoCommit(false);
		Transaction newTransaction = transactionFactory.newTransaction(sqlSession.getConnection());
		
		try {
			int sid = tMushroomServiceDao.add(sqlSession,tMushroomService);
			if (0 == sid) {
				throw new RuntimeException("添加t_mushroom_service信息失败！");
			}
			int tid = tMushroomTableDao.add(sqlSession,tMushroomTable);
			if (0 == tid) { 
				throw new RuntimeException("添加t_mushroom_table信息失败！");
			}
			TMushroomServiceTable tMushroomServiceTable = new TMushroomServiceTable();
			tMushroomServiceTable.setSid(sid);
			tMushroomServiceTable.setTid(tid);
			int insertCount = tMushroomServiceTableDao.add(sqlSession,tMushroomServiceTable);
			if (1 != insertCount) {
				throw new RuntimeException("添加t_mushroom_service_table信息失败！");
			}
			tMushroomTableField.setTid(tid);
			insertCount = tMushroomTableFieldDao.add(sqlSession,tMushroomTableField);
			if (0 >= insertCount) {
				throw new RuntimeException("添加t_mushroom_table_field信息失败！");
			}
			newTransaction.commit();
		} catch (Exception e) {
			newTransaction.rollback();
			throw new RuntimeException(e.getMessage());
		}
	}

		
}
