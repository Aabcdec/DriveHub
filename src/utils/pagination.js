// @ts-nocheck
/* eslint-disable */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
// src/api/user.js
import axios from 'axios';

export const fetchPageData = async (pageNum = 1, pageSize = 10,url,errorMas) => {
console.log(pageNum, pageSize);
  
  try {
    const res = await axios.get(url, {
      params: {
        pageNum,
        pageSize
      }
    });

    
    return res.data;
  } catch (error) {
    console.error(errorMas, error);
    throw error;
  }
};