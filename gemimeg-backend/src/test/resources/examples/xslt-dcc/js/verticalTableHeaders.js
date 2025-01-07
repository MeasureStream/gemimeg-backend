function transformToVerticalTableHeaders(selector) {
    document.querySelectorAll(selector).forEach((table) => {
        const header = table.querySelector("thead");
        const body = table.querySelector("tbody");
        const rows = body.querySelectorAll("tr")
        const headings = [];

        header.querySelectorAll("th").forEach((th) => {
            headings.push(th.innerHTML);
        });

        for (let i = 0; i < rows.length && i < headings.length; i++) {
            rows[i].insertAdjacentHTML("afterbegin", `<th>${headings[i]}</th>`);
        }

        header.remove();
    })
}