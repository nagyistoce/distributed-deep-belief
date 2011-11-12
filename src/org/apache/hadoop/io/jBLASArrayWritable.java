package org.apache.hadoop.io;
import org.apache.hadoop.io.Writable;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import org.jblas.*;

public class jBLASArrayWritable implements Writable {
	
	private ArrayList<DoubleMatrix> mlist;
	
	public jBLASArrayWritable(DoubleMatrix[] mlist) {
		this.mlist = new ArrayList<DoubleMatrix>(Arrays.asList(mlist));
	}
	
	public jBLASArrayWritable() {
		this.mlist = new ArrayList<DoubleMatrix>();
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		int size = in.readInt();
		mlist.ensureCapacity(size);
		for(int i = 0; i < size; i++) {
			mlist.add(readMatrix(in));
		}
	}

	private DoubleMatrix readMatrix(DataInput in) throws IOException {
		int rows,cols;
		rows = in.readInt();
		cols = in.readInt();
		
		if((rows == 0)||(cols == 0)) {
			return null;
		}
		
		double[][] temp = new double[rows][cols];
		
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				temp[i][j] = in.readDouble();
		return new DoubleMatrix(temp);
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(mlist.size());
		for(DoubleMatrix M : mlist) {
			writeMatrix(out,M);
		}
	}
	
	private void writeMatrix(DataOutput out, DoubleMatrix M) throws IOException {
		if(M == null) {
			return;
		}
		int rows = M.rows,cols = M.columns;
		out.writeInt(rows);
		out.writeInt(cols);
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				out.writeDouble(M.get(rows, cols));
			}
		}
		return;
	}
	
	public MatrixArrayWritable read(ObjectInputStream in) throws IOException, ClassNotFoundException {
		MatrixArrayWritable w = new MatrixArrayWritable();
		w.readFields(in);
		return w;
	}
	
	public ArrayList<DoubleMatrix> getData() {
		return (ArrayList<DoubleMatrix>) mlist.clone();		
	}
}
