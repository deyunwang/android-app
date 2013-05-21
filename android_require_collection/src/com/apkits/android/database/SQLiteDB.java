/**
 * Copyright (C) 2012 TookitForAndroid Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.apkits.android.database;

import com.apkits.android.common.CommonRegex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 创建数据库辅助工具，用于创建，打开和管理一个数据库。
 * @author wangdeyun
 */
public class SQLiteDB extends SQLiteOpenHelper  {

	/** 调试输出标签 */
	private static final String TAG = "SQLiteHelper";
	
	/** SQLite数据库对象 */
	private SQLiteDatabase mDatabase;
	
	/**  是否打印SQL语句开关  */
	private boolean mPrintSQL = false;
	
	/**
	 * 数据库在没有调用getWritableDatabase() 或者 getReadableDatabase(),其中的一个方法前，是不会创建或者打开的。
	 * @param context 		Android应用环境变量引用,用于创建或者打开数据库
	 * @param dbName 		数据库名称，即数据库文件名。如果为null则创建一个内存数据库。
	 * @param factory 		用于创建Cursor游标对象组，默认为null。
	 * @param version 		数据库版本，从1开始。如果已经存在的数据库版本比version要小，则会调用 
	 * 			  			onUpgrade(SQLiteDatabase, int, int)升级数据库。否则调用 onDowngrade(SQLiteDatabase, int, int)
	 * 			  			降级数据库。
	 */
	public SQLiteDB(Context context, String dbName, CursorFactory factory,int version) {
		super(context, dbName, factory, version);
	}
	
	/**
	 * </br><b>description : </b>	数据库在没有调用getWritableDatabase() 或者 getReadableDatabase(),其中的一个方法前，是不会创建或者打开的。
	 * @param context 				Android应用环境变量引用,用于创建或者打开数据库
	 * @param dbName 				数据库名称，即数据库文件名。如果为null则创建一个内存数据库。
	 * @param version 				数据库版本，从1开始。如果已经存在的数据库版本比version要小，则会调用 
	 * 			  onUpgrade(SQLiteDatabase, int, int)升级数据库。否则调用 onDowngrade(SQLiteDatabase, int, int)
	 * 			  降级数据库。
	 */
	public SQLiteDB(Context context, String dbName,int version) {
		this(context, dbName, null, version);
	}
	
	/**
	 * <b>title : 	</b>		取得数据库对象
	 * </br><b>description :</b>取得原始数据库对象
	 * </br><b>time :</b>		2012-7-30 下午11:38:04
	 * @return
	 */
	public SQLiteDatabase getDatabase(){
		return mDatabase;
	}
	
	/**
	 * <b>description :</b>		打印SQL语句
	 * </br><b>time :</b>		2012-7-28 下午11:18:14
	 */
	public void enablePrintSQL(){
		mPrintSQL = true;
	}
	
	/**
	 * <b>description :</b>		如果数据库没有被创建，调用此方法将会触发OnCreate()方法创建数据库。
	 * 需要 onCreateSQLFile() 	返回创建数据库时执行的SQL文件。
	 * </br><b>time :</b>		2012-7-8 下午2:30:13
	 */
	final public void verify(){
		mDatabase = this.getReadableDatabase();
		mDatabase.rawQuery("SELECT * FROM sqlite_master;",null).close();
	}
	
	/**
	 * </br><b>description :</b>插入数据
	 * </br><b>time :</b>		2012-7-8 下午2:29:37
	 * @param table 			需要插入数据的表名
	 * @param values 			需要插入的Key-Value键值对。Map对象包含数据行的初始值。
	 *            			Key必须是表中的列名，Value必须是表中的列值。
	 * @return 				返回最后插入的行ID。如果发生异常，返回 -1 。
	 */
	public long insert(String table, ContentValues values) {
		if(null == mDatabase || mDatabase.isReadOnly()){
			mDatabase = this.getWritableDatabase();
		}
		return mDatabase.insert(table, null, values);
	}
	
	
	/**
	 * <b>description :</b>		根据Key和Value自动判断插入或者更新
	 * </br><b>time :</b>		2012-8-10 下午9:59:44
	 * @param table
	 * @param values
	 * @param keyName
	 * @param keyValue
	 * @return
	 */
	public long insertOrUpdate(String table, ContentValues values,
			String keyName, Object keyValue) {
		String[] columns = new String[] { keyName };
		String selection = keyName + "=?";
		String[] selectionArgs = toArgs(keyValue);
		boolean isExist = query(table, columns, selection, selectionArgs).getCount() > 0;
		if (isExist) {
			values.remove(keyName);
			return update(table, values, selection, selectionArgs);
		} else {
			return insert(table, values);
		}
	}
	
	/**
	 * <b>description :</b>		转换为字符数组
	 * </br><b>time :</b>		2012-8-10 下午10:00:52
	 * @param values
	 * @return
	 */
	public static String[] toArgs(Object...values){
        String[] args = new String[values.length];
        for(int i=0;i<values.length;i++){
            args[i] = String.valueOf(values[i]);
        }
        return args;
    }
	
	/**
	 * </br><b>title : </b>		删除数据
	 * </br><b>description :</b>删除数据
	 * </br><b>time :</b>		2012-7-8 下午2:29:10
	 * @param table 			表名
	 * @param whereClause 		执行条件，用?置换
	 * @param whereArgs 		需要置换?的参数
	 * @return 				影响行数
	 */
	public int delete(String table, String whereClause, String[] whereArgs) {
		if(null == mDatabase || mDatabase.isReadOnly()){
			mDatabase = this.getWritableDatabase();
		}
		return mDatabase.delete(table, whereClause, whereArgs);
	}
	
	/**
	 * </br><b>title : </b>		更新数据
	 * </br><b>description :</b>更新数据
	 * </br><b>time :</b>		2012-7-8 下午2:28:28
	 * @param table 			表名
	 * @param values 			需要更新Key-Value键值对
	 * @param whereClause 		更新条件，用?置换
	 * @param whereArgs 		需要置换?的参数
	 * @return 				影响行数
	 */
	public int update(String table, ContentValues values, String whereClause,
			String[] whereArgs) {
		if(null == mDatabase || mDatabase.isReadOnly()){
			mDatabase = this.getWritableDatabase();
		}
		return mDatabase.update(table, values, whereClause, whereArgs);
	}
	
	/**
	 * </br><b>title : </b>		查询数据
	 * </br><b>description :</b>查询数据
	 * </br><b>time :</b>		2012-7-8 下午2:27:58
	 * @param sqlStatement 		SQL语句
	 * @return 				查询结果游标对象
	 */
	public Cursor query(String sqlStatement) {
		mDatabase = this.getReadableDatabase();
		return mDatabase.rawQuery(sqlStatement, null);
	}

	/**
	 * </br><b>title : </b>		查询数据
	 * </br><b>description :</b>查询数据
	 * </br><b>time :</b>		2012-7-8 下午2:27:14
	 * @param table 			表名
	 * @param columns 			要获取的字段名
	 * @param selection 		查询条件
	 * @param selectionArgs 	条件参数
	 * @return 				返回查询结果游标对象
	 */
	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs) {
		mDatabase = this.getReadableDatabase();
		return mDatabase.query(table, columns, selection, selectionArgs, null, null,null); 
	}
	
	/**
	 * </br><b>title : </b>		查询数据
	 * </br><b>description :</b>查询数据
	 * </br><b>time :</b>		2012-7-8 下午2:27:14
	 * @param table 			表名
	 * @param columns 			要获取的字段名
	 * @param selection 		查询条件
	 * @param selectionArgs 	条件参数
	 * @param orderBy		 	排序参数
	 * @return 				返回查询结果游标对象
	 */
	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs,String orderBy) {
		mDatabase = this.getReadableDatabase();
		return mDatabase.query(table, columns, selection, selectionArgs, null, null,orderBy); 
	}
	
	/**
	 * </br><b>title : </b>		执行单句SQL语句
	 * </br><b>description :</b>执行单句SQL语句，不能是SELECT或者其它带返回数据的SQL语句。
	 * </br><b>time :</b>		2012-7-8 下午2:26:45
	 * @param sql 				需要被执行的SQL语句。不支持用分号（;）分隔的多行语句。
	 */
	public void executeSQL(String sql){
		if(null == mDatabase || mDatabase.isReadOnly()){
			mDatabase = this.getWritableDatabase();
		}
		if(mPrintSQL) Log.i(TAG,String.format("[Executing SQL] : %s", sql));
		mDatabase.execSQL(sql);
	}
	
	/**
	 * </br><b>title : </b>		执行单句SQL语句
	 * </br><b>description :</b>执行单句SQL语句，不能是SELECT/INSERT/UPDATE/DELETE等。
	 * </br><b>time :</b>		2012-7-8 下午2:26:04
	 * @param sql 				需要被执行的SQL语句。不支持用分号（;）分隔的多行语句。
	 * @param bindArgs 			只能是byte[], String, Long 和 Double 等类型
	 */
	public void executeSQL(String sql,Object[] bindArgs){
		if(null == mDatabase || mDatabase.isReadOnly()){
			mDatabase = this.getWritableDatabase();
		}
		if(mPrintSQL) Log.i(TAG,String.format("[Executing SQL] : %s", sql));
		mDatabase.execSQL(sql, bindArgs);
	}
	
	/**
	 * </br><b>title : </b>		 开启事务
	 * </br><b>description :</b> 开启事务
	 * </br><b>time :</b>		2012-7-30 下午11:40:39
	 */
	public void beginTransaction() {
		if (null != mDatabase) {
			mDatabase.beginTransaction();
		}
	}

	/**
	 * </br><b>title : </b>		设置事务标志为成功，当结束事务时就会提交事务
	 * </br><b>description :</b>设置事务标志为成功，当结束事务时就会提交事务
	 * </br><b>time :</b>		2012-7-30 下午11:40:54
	 */
	public void setTransactionSuccessful() {
		if (null != mDatabase) {
			mDatabase.setTransactionSuccessful();
		}
	}

	/**
	 * </br><b>title : </b>		结束事务
	 * </br><b>description :</b>结束事务
	 * </br><b>time :</b>		2012-7-30 下午11:41:10
	 */
	public void endTransaction() {
		if (null != mDatabase) {
			mDatabase.endTransaction();
		}
	}
	
	/**
	 * 关闭数据库
	 * 在使用完数据库后，必须手动关闭。
	 */
	final public void close(){
		if( null != mDatabase ){
			mDatabase.close();
		}
	}

	/**
	 * SQ语句组
	 */
	private StringBuffer mSQLStatements = new StringBuffer();
	
	/**
	 * </br><b>title : </b>		设置SQL语句组
	 * </br><b>description :</b>设置SQL语句组，以\n或者\r\n和;分行。
	 * </br><b>time :</b>		2012-7-8 下午2:25:30
	 * @param sqlStatements 	SQL语句组
	 */
	public void addSQLStatement(String sqlStatements){
		mSQLStatements.append(sqlStatements);
	}
	
	/**
	 * 当数据库被第一次创建时调用。
	 * 在此方法中可创建表和初始化表数据。
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		if( null == mSQLStatements || mSQLStatements.length() <= 0){
			Log.w(TAG," Empty SQL statements for database create/upgrade !");
		}else{
			execSQLStatements(db,formateToLine(mSQLStatements.toString()));
		}
	}
	
	/**
	 * </br><b>title : </b>		将SQL语句格式化成每行一句
	 * </br><b>description :</b>将SQL语句格式化成每行一句
	 * </br><b>time :</b>		2012-7-28 下午11:04:56
	 * @param sqlGroup
	 * @return
	 */
	private String[] formateToLine(String sqlGroup){
		StringBuffer sqlTemp = new StringBuffer();
		for(String line : sqlGroup.split(System.getProperty("line.separator"))){
			if( !CommonRegex.matchEmptyLine(line) && !line.startsWith("--") ){
				sqlTemp.append(line);
			}
		}
		return sqlTemp.toString().split(";");
	}
	
	/**
	 * 升级数据库
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}
	
	/**
	 * </br><b>title : </b>		执行一组SQL语句
	 * </br><b>description :</b>执行一组SQL语句
	 * </br><b>time :</b>		2012-7-8 下午2:24:27
	 * @param db 				数据库对象
	 * @param sqlStatements 	SQL语句组
	 */
	public void execSQLStatements(SQLiteDatabase db,String[] sqlStatements){
		for(String sql : sqlStatements){
			sql = sql.endsWith(";") ? sql : (sql+=";");
			if(mPrintSQL) Log.i(TAG,String.format("[Executing SQL] : %s", sql));
			db.execSQL(sql);
		}
	}

}
