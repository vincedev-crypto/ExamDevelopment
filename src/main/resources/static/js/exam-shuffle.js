document.addEventListener('DOMContentLoaded', function() {
    // Fisher-Yates Shuffle for HTML Elements
    function shuffleElements(container) {
        var elements = Array.from(container.children);
        for (var i = elements.length - 1; i > 0; i--) {
            var j = Math.floor(Math.random() * (i + 1));
            container.appendChild(elements[j]);
        }
    }

    // Find all MC option containers and shuffle them
    var mcContainers = document.querySelectorAll('.mc-option-container');
    mcContainers.forEach(function(container) {
        shuffleElements(container);
    });
});
