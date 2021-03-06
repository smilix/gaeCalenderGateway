'use strict';

angular.module('frontendApp').service('Utils',
  function Utils($q, GlobalError) {
    this.handleError = function (msg) {
      GlobalError.show(msg);
    };

    this.handleResponse = function (httpPromise, errorMsg) {
      var defer = $q.defer();

      httpPromise.success(function (data, status, headers, config) {
        defer.resolve(data);
      }).
        error(function (data, status, headers, config) {
          defer.reject(errorMsg + (data.msg || data));
        });

      return defer.promise;
    };

    this.shorter = function (text, maxLen) {
      if (typeof text !== 'string') {
        return '';
      }
      if (text.length <= maxLen - 3) {
        return text;
      }
      return text.substring(0, maxLen - 3) + '...';
    };

    /**
     * Converts any non string argument to string (except null, undefined). See the test for examples.
     *
     * @param {string} text
     * @returns {boolean} true
     */
    this.emptyStr = function (text) {
      return text === undefined || text === null || text.toString().length === 0;
    };
  });
