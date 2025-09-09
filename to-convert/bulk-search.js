(function () {
    function saveResponse(author, data) {
        fetch('save-response', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ author: author, data: data })
        }).catch(function (err) {
            console.error('Save failed', err);
        });
    }

    function BulkSearchModel() {
        var self = this;
        self.authorsText = ko.observable('');
        self.isRunning = ko.observable(false);
        self.progress = ko.observable(0);
        self.total = ko.observable(0);
        self.rows = ko.observableArray([]);
        self.progressWidth = ko.computed(function () {
            return self.total() ? (self.progress() / self.total() * 100) + '%' : '0%';
        });

        self.start = function () {
            if (self.isRunning()) return;
            var list = parseAuthors(self.authorsText());
            if (list.length === 0) {
                alert('Please enter author names');
                return;
            }
            self.total(list.length);
            self.progress(0);
            self.rows.removeAll();
            self.isRunning(true);
            processNext(0, list);
        };

        self.stop = function () {
            self.isRunning(false);
        };

        function processNext(idx, list) {
            if (!self.isRunning() || idx >= list.length) {
                self.isRunning(false);
                return;
            }
            var author = list[idx];
            var position = 1;
            fetch('search', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ author: author })
            }).then(function (r) { return r.json(); })
              .then(function (data) {
                  var top = getTopResults(data.results);
                  if (top.length === 0) {
                      self.rows.push({
                          position: '',
                          author: author,
                          title: '',
                          url: '',
                          domain_country: '',
                          snippet: '',
                          is_exact_match: '',
                          ai_verified: '',
                          success: data.success,
                          total_results: 0,
                          processing_time_ms: data.processing_time_ms,
                          ai_analysis_used: data.ai_analysis_used,
                          search_engine: data.metadata && data.metadata.search_engine,
                          filters_applied: Array.isArray(data.metadata && data.metadata.filters_applied) ? data.metadata.filters_applied.join(', ') : '',
                          timestamp: data.metadata && data.metadata.timestamp
                      });
                  } else {
                        for (var i = 0; i < top.length; i++) {
                            var item = top[i];
                            self.rows.push({
                                position: position++,
                                author: author,
                                title: item.title || '',
                                url: item.url || item.link || '',
                                domain_country: getDomainCountry(item.url || item.link || ''),
                                snippet: item.snippet || '',
                                is_exact_match: item.is_exact_match,
                                ai_verified: item.ai_verified,
                                success: data.success,
                                total_results: data.total_results,
                                processing_time_ms: data.processing_time_ms,
                                ai_analysis_used: data.ai_analysis_used,
                                search_engine: data.metadata && data.metadata.search_engine,
                                filters_applied: Array.isArray(data.metadata && data.metadata.filters_applied) ? data.metadata.filters_applied.join(', ') : '',
                                timestamp: data.metadata && data.metadata.timestamp
                            });
                        }
                    }
                  saveResponse(author, data);
              }).catch(function (err) {
                  console.error('Search failed', err);
              }).finally(function () {
                  self.progress(idx + 1);
                  processNext(idx + 1, list);
              });
        }
    }

    window.addEventListener('DOMContentLoaded', function () {
        ko.applyBindings(new BulkSearchModel());
    });
})();
